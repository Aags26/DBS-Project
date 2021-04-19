package com.bphc.dbs_project.fragments.patient.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bphc.dbs_project.R;
import com.bphc.dbs_project.adapters.AppointmentAdapterPatient;
import com.bphc.dbs_project.helper.APIClient;
import com.bphc.dbs_project.helper.OnItemClickListener;
import com.bphc.dbs_project.helper.Webservices;
import com.bphc.dbs_project.models.Appointment;
import com.bphc.dbs_project.models.ServerResponse;
import com.bphc.dbs_project.prefs.SharedPrefs;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bphc.dbs_project.prefs.SharedPrefsConstants.ID;

public class AppointmentsFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerAppointments;
    private AppointmentAdapterPatient appointmentAdapterPatient;
    private Webservices webservices;
    private ArrayList<Appointment> appointments, previousAppointments, upcomingAppointments;
    private TextView textPreviousAppointments, textUpcomingAppointments;
    private boolean isPreviousSelected = false, isUpcomingSelected = false;
    private int f = 1;

    private ImageView noAppointmentsImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_appointments, container, false);

        recyclerAppointments = view.findViewById(R.id.recycler_patient_appointment);
        textPreviousAppointments = view.findViewById(R.id.text_previous_appointments);
        textUpcomingAppointments = view.findViewById(R.id.text_upcoming_appointments);

        noAppointmentsImage = view.findViewById(R.id.img_no_appointments);

        getAllPatientAppointments();

        appointments = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        appointmentAdapterPatient = new AppointmentAdapterPatient(appointments, 1, requireContext());
        recyclerAppointments.setLayoutManager(layoutManager);
        recyclerAppointments.setAdapter(appointmentAdapterPatient);

        appointmentAdapterPatient.setOnItemClickListener(listener);
        textPreviousAppointments.setOnClickListener(this);
        textUpcomingAppointments.setOnClickListener(this);


        return view;
    }

    OnItemClickListener listener = new OnItemClickListener() {

        @Override
        public void onItemClick(int position) {
            super.onItemClick(position);
            deleteAppointment(position);
        }

    };

    @Override
    public void onClick(View v) {
        appointments.clear();
        if (((TextView) v).getText().toString().equals("Previous")) {
            f = 0;
            if (!isPreviousSelected) {
                isPreviousSelected = true;
                isUpcomingSelected = false;
                addAppointments(previousAppointments, textPreviousAppointments, textUpcomingAppointments);
            }
        } else {
            f = 1;
            isUpcomingSelected = true;
            isPreviousSelected = false;
            addAppointments(upcomingAppointments, textUpcomingAppointments, textPreviousAppointments);
        }

    }

    private void getAllPatientAppointments() {

        webservices = APIClient.getRetrofitInstance().create(Webservices.class);
        Call<ServerResponse> call = webservices.getAllPatientAppointments(SharedPrefs.getIntParams(requireContext(), ID));

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                previousAppointments = new ArrayList<>();
                upcomingAppointments = new ArrayList<>();
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (!serverResponse.isError()) {
                        previousAppointments.addAll(serverResponse.getResult().getPreviousAppointments());
                        upcomingAppointments.addAll(serverResponse.getResult().getUpcomingAppointments());

                        addAppointments(upcomingAppointments, textUpcomingAppointments, textPreviousAppointments);
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });

    }

    private void deleteAppointment(int position) {
        Appointment appointment = upcomingAppointments.get(position);
        String time = appointment.getTime();
        int slot = Integer.parseInt(time.split(" ")[1].split(":")[0]);
        String prevTime = "";
        if (time.split(" ")[2].equals("pm"))
            prevTime = time.split(" ")[0] + " " + (slot + 12) + ":00:00";
        else
            prevTime = time.split(" ")[0] + " " + slot + ":00:00";
        Call<ServerResponse> call = webservices.deleteAppointment(
                SharedPrefs.getIntParams(requireContext(), ID),
                appointment.getDoctor().getDocId(),
                slot,
                prevTime
        );
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (!serverResponse.isError()) {
                        upcomingAppointments.remove(position);
                        appointments.clear();
                        appointments.addAll(upcomingAppointments);
                        appointmentAdapterPatient.notifyItemRemoved(position);
                        checkEmpty(appointments);
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });

    }

    private void addAppointments(ArrayList<Appointment> listToBeAdded, TextView v1, TextView v2) {
        checkEmpty(listToBeAdded);
        appointments.addAll(listToBeAdded);
        v1.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.custom_text_filled));
        v1.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        v2.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.custom_text_stroke));
        v2.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500));

        if (appointments.isEmpty())
            appointmentAdapterPatient.notifyDataSetChanged();
        else {
            appointmentAdapterPatient = new AppointmentAdapterPatient(appointments, f, requireContext());
            recyclerAppointments.setAdapter(appointmentAdapterPatient);
            appointmentAdapterPatient.notifyItemRangeInserted(0, appointments.size());
        }
        appointmentAdapterPatient.setOnItemClickListener(listener);
    }
    private void checkEmpty(ArrayList<Appointment> listToBeAdded) {
        if (listToBeAdded.isEmpty()) {
            noAppointmentsImage.setVisibility(View.VISIBLE);
            recyclerAppointments.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        } else {
            noAppointmentsImage.setVisibility(View.GONE);
            recyclerAppointments.setBackgroundColor(Color.parseColor("#EDF1FD"));
        }
    }
}
