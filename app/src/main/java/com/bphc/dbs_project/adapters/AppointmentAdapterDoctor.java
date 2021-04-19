package com.bphc.dbs_project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bphc.dbs_project.R;
import com.bphc.dbs_project.helper.OnItemClickListener;
import com.bphc.dbs_project.models.Appointment;
import com.bphc.dbs_project.models.Doctor;
import com.bphc.dbs_project.models.Patient;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppointmentAdapterDoctor extends RecyclerView.Adapter<AppointmentAdapterDoctor.AppointmentDoctorViewHolder> {

    private ArrayList<Appointment> appointments;
    private OnItemClickListener listener;
    private Context context;

    public AppointmentAdapterDoctor(ArrayList<Appointment> appointments, Context context) {
        this.appointments = appointments;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class AppointmentDoctorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemClickListener listener;
        TextView day, time, patientName, patientPhone, patientEmail, patientAge, patientAddress;
        CircleImageView patientImage;

        public AppointmentDoctorViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            this.listener = listener;

            day = itemView.findViewById(R.id.text_appointment_day);
            time = itemView.findViewById(R.id.text_appointment_time);
            patientName = itemView.findViewById(R.id.appointment_patient_name);
            patientPhone = itemView.findViewById(R.id.appointment_patient_phone);
            patientEmail = itemView.findViewById(R.id.appointment_patient_email);
            patientAge = itemView.findViewById(R.id.appointment_patient_age);
            patientAddress = itemView.findViewById(R.id.appointment_patient_address);

            patientImage = itemView.findViewById(R.id.patient_image);
            itemView.findViewById(R.id.card_patient).setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            }
        }
    }

    @NonNull
    @Override
    public AppointmentDoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_doctor_appointment, parent, false);
        return new AppointmentDoctorViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentDoctorViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);

        String time = appointment.getTime();
        SimpleDateFormat sdf1 = new SimpleDateFormat("d-MM-yyyy", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());

        try {
            holder.day.setText(sdf2.format(sdf1.parse(time.split(" ")[0])));
            holder.time.setText(time.split(" ")[1] + " " + time.split(" ")[2]);
        } catch (Exception e) {

        }
        Patient patient = appointment.getPatient();
        holder.patientName.setText(patient.getName());
        holder.patientEmail.setText(patient.getEmail());
        holder.patientPhone.setText(patient.getPhone());
        holder.patientAge.setText(patient.getAge() + "");
        holder.patientAddress.setText(patient.getAddress());

        Glide.with(context).load(patient.getImage()).into(holder.patientImage);

    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }
}
