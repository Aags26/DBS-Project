package com.bphc.dbs_project.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bphc.dbs_project.R;
import com.bphc.dbs_project.helper.OnItemClickListener;
import com.bphc.dbs_project.models.Hospital;

import java.util.ArrayList;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder> {

    private ArrayList<Hospital> hospitals;
    private OnItemClickListener listener;

    public HospitalAdapter(ArrayList<Hospital> hospitals) {
        this.hospitals = hospitals;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class HospitalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemClickListener listener;
        TextView name, address, count;

        public HospitalViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            this.listener = listener;

            name = itemView.findViewById(R.id.text_hospital_name);
            address = itemView.findViewById(R.id.text_hospital_address);
            count = itemView.findViewById(R.id.doctor_count);

            itemView.findViewById(R.id.card_hospital).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position, 1);
                }
            }
        }
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_hospital, parent, false);
        return new HospitalViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalViewHolder holder, int position) {
        Hospital hospital = hospitals.get(position);
        holder.name.setText(hospital.getHospitalName().trim());
        holder.address.setText(hospital.getHospitalStreet().trim() + ", " + hospital.getHospitalArea().trim());
        int count = hospital.getCount();
        if (count == 1)
            holder.count.setText(hospital.getCount() + " Doctor");
        else
            holder.count.setText(hospital.getCount() + " Doctors");
    }

    @Override
    public int getItemCount() {
        return hospitals.size();
    }
}
