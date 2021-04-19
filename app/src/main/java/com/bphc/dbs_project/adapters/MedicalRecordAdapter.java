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
import com.bphc.dbs_project.models.MedicalRecord;
import com.bphc.dbs_project.prefs.SharedPrefs;

import java.util.ArrayList;

import static com.bphc.dbs_project.prefs.SharedPrefsConstants.PROFESSION;

public class MedicalRecordAdapter extends RecyclerView.Adapter<MedicalRecordAdapter.MedicalRecordViewHolder> {

    private ArrayList<MedicalRecord> medicalRecords;
    private OnItemClickListener listener;
    private Context context;

    public MedicalRecordAdapter(ArrayList<MedicalRecord> medicalRecords, Context context) {
        this.medicalRecords = medicalRecords;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class MedicalRecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemClickListener listener;
        Context context;
        TextView diagnosis, date, doctor, description, attachment;
        ImageView deleteImage;

        public MedicalRecordViewHolder(@NonNull View itemView, OnItemClickListener listener, Context context) {
            super(itemView);

            this.listener = listener;
            this.context = context;

            diagnosis = itemView.findViewById(R.id.text_diagnosis);
            date = itemView.findViewById(R.id.text_diagnosis_date);
            doctor = itemView.findViewById(R.id.text_diagnosis_doctor);
            description = itemView.findViewById(R.id.text_diagnosis_description);
            attachment = itemView.findViewById(R.id.text_diagnosis_attachment);

            deleteImage = itemView.findViewById(R.id.image_delete);
            if (SharedPrefs.getStringParams(context, PROFESSION, "").equals("patient"))
                deleteImage.setVisibility(View.VISIBLE);
            else
                deleteImage.setVisibility(View.INVISIBLE);

            deleteImage.setOnClickListener(this);

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
    public MedicalRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_medical_record, parent, false);
        return new MedicalRecordViewHolder(view, listener, context);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicalRecordViewHolder holder, int position) {
        MedicalRecord medicalRecord = medicalRecords.get(position);
        holder.diagnosis.setText(medicalRecord.getDiagnosis());
        holder.date.setText(medicalRecord.getDoe());
        holder.doctor.setText(medicalRecord.getDescription().split("\\|")[0]);
        holder.description.setText(medicalRecord.getDescription().split("\\|")[1]);
        holder.attachment.setText(medicalRecord.getAttachment());
    }

    @Override
    public int getItemCount() {
        return medicalRecords.size();
    }
}
