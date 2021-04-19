package com.bphc.dbs_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bphc.dbs_project.adapters.AppointmentAdapterDoctor;
import com.bphc.dbs_project.adapters.AppointmentAdapterPatient;
import com.bphc.dbs_project.helper.APIClient;
import com.bphc.dbs_project.helper.OnItemClickListener;
import com.bphc.dbs_project.helper.Webservices;
import com.bphc.dbs_project.models.Appointment;
import com.bphc.dbs_project.models.Doctor;
import com.bphc.dbs_project.models.ServerResponse;
import com.bphc.dbs_project.prefs.SharedPrefs;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bphc.dbs_project.prefs.SharedPrefsConstants.DEPARTMENT;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.ID;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.NAME;

public class DoctorActivity extends AppCompatActivity implements View.OnClickListener {

    private AppointmentAdapterDoctor appointmentAdapterDoctor;
    private TextView doctorName, doctorDepartment;
    private TextView previous, today, upcoming;
    private boolean isPreviousSelected = false, isTodaySelected = false, isUpcomingSelected = false;
    private ArrayList<Appointment> appointments, previousAppointments, todayAppointments, upcomingAppointments;

    private ImageView noAppointmentsImage;
    private RecyclerView recyclerAppointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        doctorName = findViewById(R.id.text_name_doctor);
        doctorName.setText(SharedPrefs.getStringParams(this, NAME, ""));

        doctorDepartment = findViewById(R.id.text_profession_doctor);
        doctorDepartment.setText(SharedPrefs.getStringParams(this, DEPARTMENT, ""));

        previous = findViewById(R.id.text_previous_appointments);
        today = findViewById(R.id.text_today_appointments);
        upcoming = findViewById(R.id.text_upcoming_appointments);

        noAppointmentsImage = findViewById(R.id.img_no_appointments);

        ImageView profile = findViewById(R.id.doctor_profile);
        profile.setOnClickListener(v -> startActivity(new Intent(DoctorActivity.this, DoctorProfileActivity.class)));

        getDoctorAppointments();

        appointments = new ArrayList<>();
        recyclerAppointments = findViewById(R.id.recycler_doctor_appointments);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        appointmentAdapterDoctor = new AppointmentAdapterDoctor(appointments, this);
        recyclerAppointments.setLayoutManager(layoutManager);
        recyclerAppointments.setAdapter(appointmentAdapterDoctor);

        appointmentAdapterDoctor.setOnItemClickListener(listener);

        previous.setOnClickListener(this);
        today.setOnClickListener(this);
        upcoming.setOnClickListener(this);

    }

    private void getDoctorAppointments() {

        previousAppointments = new ArrayList<>();
        todayAppointments = new ArrayList<>();
        upcomingAppointments = new ArrayList<>();


        Webservices webservices = APIClient.getRetrofitInstance().create(Webservices.class);
        Call<ServerResponse> call = webservices.getDoctorAppointments(
                SharedPrefs.getIntParams(this, ID)
        );
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (!serverResponse.isError()) {
                        previousAppointments.addAll(serverResponse.getResult().getPreviousAppointments());
                        todayAppointments.addAll(serverResponse.getResult().getTodayAppointments());
                        upcomingAppointments.addAll(serverResponse.getResult().getUpcomingAppointments());

                        addAppointments(todayAppointments, today, previous, upcoming);
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    OnItemClickListener listener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            super.onItemClick(position);
            Appointment appointment;
            if (isPreviousSelected)
                appointment = previousAppointments.get(position);
            else if (isTodaySelected)
                appointment = todayAppointments.get(position);
            else
                appointment = upcomingAppointments.get(position);

            Intent intent = new Intent(DoctorActivity.this, PatientDetailsActivity.class);
            intent.putExtra("appointment", appointment);
            startActivity(intent);
        }
    };

    @Override
    public void onClick(View v) {
        appointments.clear();
        appointmentAdapterDoctor.notifyDataSetChanged();
        if (((TextView)v).getText().toString().equals("Previous")) {
            if (!isPreviousSelected) {
                isPreviousSelected = true;
                isTodaySelected = false;
                isUpcomingSelected = false;
                addAppointments(previousAppointments, previous, today, upcoming);
            }
        } else if (((TextView)v).getText().toString().equals("Today")) {
            if (!isTodaySelected) {
                isPreviousSelected = false;
                isTodaySelected = true;
                isUpcomingSelected = false;
                addAppointments(todayAppointments, today, upcoming, previous);
            }
        } else {
            if (!isUpcomingSelected) {
                isPreviousSelected = false;
                isTodaySelected = false;
                isUpcomingSelected = true;
                addAppointments(upcomingAppointments, upcoming, previous, today);
            }
        }
    }

    private void addAppointments(ArrayList<Appointment> listToBeAdded, TextView v1, TextView v2, TextView v3) {
        checkEmpty(listToBeAdded);
        appointments.addAll(listToBeAdded);
        v1.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_text_filled));
        v1.setTextColor(ContextCompat.getColor(this, R.color.white));
        v2.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_text_stroke));
        v2.setTextColor(ContextCompat.getColor(this, R.color.purple_500));
        v3.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_text_stroke));
        v3.setTextColor(ContextCompat.getColor(this, R.color.purple_500));

        if (appointments.isEmpty())
            appointmentAdapterDoctor.notifyDataSetChanged();
        else {
            appointmentAdapterDoctor.notifyItemRangeInserted(0, appointments.size());
        }

    }

    private void checkEmpty(ArrayList<Appointment> listToBeAdded) {
        if (listToBeAdded.isEmpty()) {
            noAppointmentsImage.setVisibility(View.VISIBLE);
            recyclerAppointments.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        } else {
            noAppointmentsImage.setVisibility(View.GONE);
            recyclerAppointments.setBackgroundColor(Color.parseColor("#EDF1FD"));
        }
    }
}