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
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppointmentAdapterPatient extends RecyclerView.Adapter<AppointmentAdapterPatient.AppointmentPatientViewHolder> {

    private ArrayList<Appointment> appointments;
    private OnItemClickListener listener;
    private int f;
    private Context context;

    public AppointmentAdapterPatient(ArrayList<Appointment> appointments, int f, Context context) {
        this.appointments = appointments;
        this.f = f;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class AppointmentPatientViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemClickListener listener;
        TextView day, time, docName, docPhone, docEmail, docDept, docHosp;
        TextView cancel;
        CircleImageView doctorImage;

        public AppointmentPatientViewHolder(@NonNull View itemView, OnItemClickListener listener, int f) {
            super(itemView);
            this.listener = listener;

            day = itemView.findViewById(R.id.text_appointment_day);
            time = itemView.findViewById(R.id.text_appointment_time);
            docName = itemView.findViewById(R.id.appointment_doctor_name);
            docPhone = itemView.findViewById(R.id.appointment_doctor_phone);
            docEmail = itemView.findViewById(R.id.appointment_doctor_email);
            docDept = itemView.findViewById(R.id.appointment_doctor_department);
            docHosp = itemView.findViewById(R.id.appointment_doctor_hospital);

            cancel = itemView.findViewById(R.id.text_appointment_cancel);
            doctorImage = itemView.findViewById(R.id.doctor_image);

            if (f == 0) {
                cancel.setVisibility(View.GONE);
            } else {
                cancel.setVisibility(View.VISIBLE);
            }

            cancel.setOnClickListener(this);

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
    public AppointmentPatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_patient_appointment, parent, false);
        return new AppointmentPatientViewHolder(view, listener, f);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentPatientViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);

        String time = appointment.getTime();
        SimpleDateFormat sdf1 = new SimpleDateFormat("d-MM-yyyy", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());

        try {
            holder.day.setText(sdf2.format(sdf1.parse(time.split(" ")[0])));
            holder.time.setText(time.split(" ")[1] + " " + time.split(" ")[2]);
        } catch (Exception e) {

        }
        Doctor doctor = appointment.getDoctor();
        holder.docName.setText(doctor.getName());
        holder.docEmail.setText(doctor.getEmail());
        holder.docPhone.setText(doctor.getPhone());
        holder.docDept.setText(doctor.getDepartment().getName());
        holder.docHosp.setText(doctor.getHospital().getHospitalName());
        Glide.with(context).load(doctor.getImage()).into(holder.doctorImage);

    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }
}
