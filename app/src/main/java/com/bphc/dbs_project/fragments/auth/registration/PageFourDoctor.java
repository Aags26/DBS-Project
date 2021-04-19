package com.bphc.dbs_project.fragments.auth.registration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bphc.dbs_project.DoctorActivity;
import com.bphc.dbs_project.R;
import com.bphc.dbs_project.adapters.SlotAdapter;
import com.bphc.dbs_project.helper.APIClient;
import com.bphc.dbs_project.helper.OnItemClickListener;
import com.bphc.dbs_project.helper.Webservices;
import com.bphc.dbs_project.models.Doctor;
import com.bphc.dbs_project.models.Result;
import com.bphc.dbs_project.models.ServerResponse;
import com.bphc.dbs_project.prefs.SharedPrefs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bphc.dbs_project.prefs.SharedPrefsConstants.AUTH;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.DEPARTMENT;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.EMAIL;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.HOSPITAL;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.ID;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.NAME;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.PASSWORD;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.PHONE;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.SESSION_FLAG;

public class PageFourDoctor extends Fragment implements View.OnClickListener {

    private ArrayList<String> slots;
    private int[] isSlotSelected = new int[10];
    private HashMap<Integer, Integer> map = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_four_doctor, container, false);

        slots = new ArrayList<>(Arrays.asList(getActivity().getResources().getStringArray(R.array.doctor_slots)));

        RecyclerView doctorSlotsRecycler = view.findViewById(R.id.recycler_doctor_slots);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 3);
        SlotAdapter slotAdapter = new SlotAdapter(slots);
        doctorSlotsRecycler.setLayoutManager(layoutManager);
        doctorSlotsRecycler.setAdapter(slotAdapter);

        slotAdapter.setOnItemClickListener(listener);

        Button buttonRegister = view.findViewById(R.id.button_register_doctor);
        buttonRegister.setOnClickListener(this);

        return view;
    }

    OnItemClickListener listener = new OnItemClickListener() {
        @Override
        public void OnItemClick(int position, TextView slotView) {
            super.OnItemClick(position, slotView);

            assert getActivity() != null;

            if (isSlotSelected[position] == 0) {
                slotView.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.custom_text_filled));
                slotView.setTextColor(getActivity().getResources().getColor(R.color.white));
                isSlotSelected[position] = 1;
                map.put(position, Integer.parseInt(slots.get(position).split("-")[0].replaceAll("[^0-9]", "")));
            } else {
                slotView.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.custom_text_stroke));
                slotView.setTextColor(getActivity().getResources().getColor(R.color.purple_500));
                isSlotSelected[position] = 0;
                map.remove(position);
            }

        }
    };

    @Override
    public void onClick(View v) {

        Webservices webservices = APIClient.getRetrofitInstance().create(Webservices.class);
        Call<ServerResponse> call = webservices.registerDoctor(
                "Dr. " + SharedPrefs.getStringParams(requireContext(), NAME, ""),
                SharedPrefs.getStringParams(requireContext(), PHONE, ""),
                SharedPrefs.getStringParams(requireContext(), EMAIL, ""),
                SharedPrefs.getStringParams(requireContext(), PASSWORD, ""),
                SharedPrefs.getIntParams(requireContext(), AUTH),
                SharedPrefs.getStringParams(requireContext(), DEPARTMENT, ""),
                SharedPrefs.getStringParams(requireContext(), HOSPITAL, ""),
                new ArrayList<>(map.values())
        );
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    Result result = serverResponse.getResult();
                    Doctor doctor = result.getDoctor();

                    SharedPrefs.setIntParams(requireContext(), ID, doctor.getDocId());
                    SharedPrefs.setIntParams(requireContext(), SESSION_FLAG, 1);

                    Toast.makeText(requireContext(), "Successfully Registered", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(requireContext(), DoctorActivity.class));
                    requireActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }
}
