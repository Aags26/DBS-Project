package com.bphc.dbs_project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bphc.dbs_project.R;
import com.bphc.dbs_project.helper.OnItemClickListener;
import com.bphc.dbs_project.models.Doctor;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private ArrayList<Doctor> doctors;
    private OnItemClickListener listener;
    private Context context;
    private int f;

    public DoctorAdapter(ArrayList<Doctor> doctors, Context context, int f) {
        this.doctors = doctors;
        this.context = context;
        this.f = f;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemClickListener listener;
        TextView name, hospital, department, phone, email;
        Button bookAppointment;
        ImageView doctorImage;

        public DoctorViewHolder(@NonNull View itemView, OnItemClickListener listener, int f) {
            super(itemView);
            this.listener = listener;

            name = itemView.findViewById(R.id.doctor_name);
            hospital = itemView.findViewById(R.id.doctor_hospital);
            department = itemView.findViewById(R.id.doctor_department);
            phone = itemView.findViewById(R.id.doctor_phone);
            email = itemView.findViewById(R.id.doctor_email);
            bookAppointment = itemView.findViewById(R.id.button_book_appointment);
            doctorImage = itemView.findViewById(R.id.doctor_image);

            if (f==0) {
                hospital.setVisibility(View.INVISIBLE);
                department.setVisibility(View.VISIBLE);
            } else {
                hospital.setVisibility(View.VISIBLE);
                department.setVisibility(View.INVISIBLE);
            }
            bookAppointment.setOnClickListener(this);

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
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_doctor, parent, false);
        return new DoctorViewHolder(view, listener, f);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);
        holder.name.setText(doctor.getName());
        holder.hospital.setText(doctor.getHospital().getHospitalName() + " hospital");
        holder.department.setText(doctor.getDepartment().getName());
        holder.phone.setText(doctor.getPhone());
        holder.email.setText(doctor.getEmail());
        Glide.with(context).load(doctor.getImage()).into(holder.doctorImage);

    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }
}
