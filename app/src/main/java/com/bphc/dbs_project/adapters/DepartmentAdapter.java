package com.bphc.dbs_project.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bphc.dbs_project.R;
import com.bphc.dbs_project.helper.OnItemClickListener;
import com.bphc.dbs_project.models.Department;

import java.util.ArrayList;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.DepartmentViewHolder>{

    private ArrayList<Department> departments;
    private Context context;
    private OnItemClickListener listener;
    private String[] colors;

    public DepartmentAdapter(Context context, ArrayList<Department> departments) {
        this.context = context;
        this.departments = departments;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class DepartmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cardDepartment;
        TextView departmentInitial, departmentName, doctorCount;
        OnItemClickListener listener;

        public DepartmentViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            this.listener = listener;

            cardDepartment = itemView.findViewById(R.id.card_department);
            departmentInitial = itemView.findViewById(R.id.text_department_initial);
            departmentName = itemView.findViewById(R.id.model_department_name);
            doctorCount = itemView.findViewById(R.id.doctor_count);

            cardDepartment.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position, 0);
                }
            }
        }
    }

    @NonNull
    @Override
    public DepartmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_department, parent, false);
        return new DepartmentViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull DepartmentViewHolder holder, int position) {

        colors = context.getResources().getStringArray(R.array.colors);

        Department department = departments.get(position);
        char initial = department.getName().charAt(0);
        holder.departmentInitial.setText(initial + "");
        holder.departmentInitial.setBackgroundColor(Color.parseColor(colors[((int)initial - 65) % 12]));

        holder.departmentName.setText(department.getName());
        int count = department.getCount();
        if (count == 1)
            holder.doctorCount.setText(department.getCount() + " Practitioner");
        else
            holder.doctorCount.setText(department.getCount() + " Practitioners");
    }

    @Override
    public int getItemCount() {
        return departments.size();
    }

}
