package com.bphc.dbs_project.fragments.auth.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bphc.dbs_project.R;
import com.bphc.dbs_project.helper.APIClient;
import com.bphc.dbs_project.helper.Webservices;
import com.bphc.dbs_project.models.ServerResponse;
import com.bphc.dbs_project.prefs.SharedPrefs;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.bphc.dbs_project.prefs.SharedPrefsConstants.ADDRESS;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.AGE;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.AUTH;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.EMAIL;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.NAME;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.PASSWORD;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.PHONE;

public class PageThreePatient extends Fragment implements View.OnClickListener {

    private TextInputLayout inputAge, inputAddress;
    private String age, address;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_three_patient, container, false);
        inputAge = view.findViewById(R.id.age);
        inputAddress = view.findViewById(R.id.address);

        Button buttonSubmit = view.findViewById(R.id.button_submit);
        buttonSubmit.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (!validateAge() | !validateAddress())
            return;
        SharedPrefs.setIntParams(requireContext(), AGE, Integer.parseInt(age));
        SharedPrefs.setStringParams(requireContext(), ADDRESS, address);
        registerPatient();
    }

    private void registerPatient() {

        Retrofit retrofit = APIClient.getRetrofitInstance();
        Webservices webservices = retrofit.create(Webservices.class);

        Call<ServerResponse> call = webservices.registerPatient(
                SharedPrefs.getStringParams(requireContext(), NAME, ""),
                SharedPrefs.getStringParams(requireContext(), PHONE, ""),
                SharedPrefs.getIntParams(requireContext(), AGE),
                SharedPrefs.getStringParams(requireContext(), ADDRESS, ""),
                SharedPrefs.getStringParams(requireContext(), EMAIL, ""),
                SharedPrefs.getStringParams(requireContext(), PASSWORD, ""),
                SharedPrefs.getIntParams(requireContext(), AUTH)
        );
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (!serverResponse.isError())
                    Toast.makeText(requireContext(), "abcd", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });

    }

    private boolean validateAge() {
        age = inputAge.getEditText().getText().toString().trim();
        if (age.isEmpty()) {
            inputAge.setError("* Please enter your age");
            return false;
        } else {
            inputAge.setError(null);
            return true;
        }
    }

    private boolean validateAddress() {
        address = inputAddress.getEditText().getText().toString().trim();
        if (address.isEmpty()) {
            inputAddress.setError("* Please enter your address");
            return false;
        } else {
            inputAddress.setError(null);
            return true;
        }
    }
}
