package com.bphc.dbs_project.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bphc.dbs_project.R;
import com.bphc.dbs_project.helper.OnItemClickListener;

import java.util.ArrayList;

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.SlotViewHolder> {

    private ArrayList<String> slots;
    private OnItemClickListener listener;

    public SlotAdapter(ArrayList<String> slots) {
        this.slots = slots;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class SlotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemClickListener listener;
        TextView slot;

        public SlotViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            this.listener = listener;
            slot = itemView.findViewById(R.id.text_model_slot);

            slot.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                    listener.OnItemClick(position, (TextView)v);
                }
            }
        }
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_slot, parent, false);
        return new SlotViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        holder.slot.setText(slots.get(position));
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }
}
