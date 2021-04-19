package com.bphc.dbs_project.fragments.patient.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bphc.dbs_project.HospDeptActivity;
import com.bphc.dbs_project.R;
import com.bphc.dbs_project.adapters.DepartmentAdapter;
import com.bphc.dbs_project.adapters.HospitalAdapter;
import com.bphc.dbs_project.helper.APIClient;
import com.bphc.dbs_project.helper.OnItemClickListener;
import com.bphc.dbs_project.helper.Webservices;
import com.bphc.dbs_project.models.Department;
import com.bphc.dbs_project.models.Hospital;
import com.bphc.dbs_project.models.ServerResponse;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private DepartmentAdapter departmentAdapter;
    private HospitalAdapter hospitalAdapter;
    private ArrayList<Department> departments = new ArrayList<>();
    private ArrayList<Hospital> hospitals = new ArrayList<>();
    private ShimmerFrameLayout shimmerFrameLayout;
    private Webservices webservices;

    private TextView textHospitals;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_home, container, false);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_patient_home);

        webservices = APIClient.getRetrofitInstance().create(Webservices.class);
        getAllDepartments();
        getAllHospitals();

        textHospitals = view.findViewById(R.id.text_4);

        RecyclerView recyclerSpecialists = view.findViewById(R.id.recycler_specialities);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        departmentAdapter = new DepartmentAdapter(requireContext(), departments);
        recyclerSpecialists.setLayoutManager(layoutManager);
        recyclerSpecialists.setAdapter(departmentAdapter);

        RecyclerView recyclerHospitals = view.findViewById(R.id.recycler_hospitals);
        RecyclerView.LayoutManager layoutManager2 = new GridLayoutManager(requireContext(), 2);
        hospitalAdapter = new HospitalAdapter(hospitals);
        recyclerHospitals.setLayoutManager(layoutManager2);
        recyclerHospitals.setAdapter(hospitalAdapter);

        departmentAdapter.setOnItemClickListener(listener);
        hospitalAdapter.setOnItemClickListener(listener);


        return view;
    }

    OnItemClickListener listener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position, int flag) {
            super.onItemClick(position, flag);
            Intent intent = new Intent(requireContext(), HospDeptActivity.class);
            if (flag == 0) {
                Department department = departments.get(position);
                intent.putExtra("department", department);
            } else {
                Hospital hospital = hospitals.get(position);
                intent.putExtra("hospital", hospital);
            }
            startActivity(intent);
        }
    };

    private void getAllDepartments() {
        shimmerFrameLayout.startShimmerAnimation();
        webservices.getAllDepartments().enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (!serverResponse.isError()) {
                        departments.clear();
                        departments.addAll(serverResponse.getResult().getDoctorDepartments());
                        departmentAdapter.notifyItemRangeChanged(0, departments.size());
                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });

    }

    private void getAllHospitals() {

       webservices.getAllHospitals().enqueue(new Callback<ServerResponse>() {
           @Override
           public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
               ServerResponse serverResponse = response.body();
               if (serverResponse != null) {
                   if (!serverResponse.isError()) {
                       hospitals.clear();
                       hospitals.addAll(serverResponse.getResult().getDoctorHospitals());
                       hospitalAdapter.notifyItemRangeChanged(0, hospitals.size());
                       textHospitals.setVisibility(View.VISIBLE);
                   }
               }
           }

           @Override
           public void onFailure(Call<ServerResponse> call, Throwable t) {

           }
       });

    }

}
