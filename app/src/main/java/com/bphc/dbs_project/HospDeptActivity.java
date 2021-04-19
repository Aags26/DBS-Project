package com.bphc.dbs_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.bphc.dbs_project.adapters.DoctorAdapter;
import com.bphc.dbs_project.helper.APIClient;
import com.bphc.dbs_project.helper.OnItemClickListener;
import com.bphc.dbs_project.helper.Webservices;
import com.bphc.dbs_project.models.Department;
import com.bphc.dbs_project.models.Doctor;
import com.bphc.dbs_project.models.Hospital;
import com.bphc.dbs_project.models.ServerResponse;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HospDeptActivity extends AppCompatActivity {

    private ArrayList<Doctor> doctors = new ArrayList<>();
    private Department department;
    private Hospital hospital;
    private DoctorAdapter doctorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department);

        Intent intent = getIntent();
        department = intent.getParcelableExtra("department");
        hospital = intent.getParcelableExtra("hospital");

        if (hospital == null) {
            TextView departmentName = findViewById(R.id.text_department_name);
            departmentName.setText(department.getName());
        } else {
            TextView hospitalName = findViewById(R.id.text_department_name);
            hospitalName.setText(hospital.getHospitalName() + " Hospital");
        }

        getAllDoctors();

        RecyclerView hospDeptRecycler = findViewById(R.id.hosp_dept_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        if (hospital == null)
            doctorAdapter = new DoctorAdapter(doctors, this, 1);
        else
            doctorAdapter = new DoctorAdapter(doctors, this, 0);
        hospDeptRecycler.setLayoutManager(layoutManager);
        hospDeptRecycler.setAdapter(doctorAdapter);

        doctorAdapter.setOnItemClickListener(listener);

    }

    OnItemClickListener listener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            super.onItemClick(position);
            Doctor doctor = doctors.get(position);
            Intent intent = new Intent(HospDeptActivity.this, BookAppointmentActivity.class);
            intent.putExtra("doctor", doctor);
            startActivity(intent);
        }
    };

    private void getAllDoctors() {
        Webservices webservices = APIClient.getRetrofitInstance().create(Webservices.class);
        Call<ServerResponse> call;
        if (hospital == null)
            call = webservices.getDoctorsOfDepartment(department.getId());
        else
            call = webservices.getDoctorsOfHospital(hospital.getHospitalId());
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NotNull Call<ServerResponse> call, @NotNull Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (!serverResponse.isError()) {
                        doctors.addAll(serverResponse.getResult().getDoctorsOfDepartment());
                        doctorAdapter.notifyItemRangeInserted(0, doctors.size());
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

}