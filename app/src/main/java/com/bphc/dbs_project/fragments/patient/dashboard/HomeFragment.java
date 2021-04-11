package com.bphc.dbs_project.fragments.patient.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bphc.dbs_project.R;
import com.facebook.shimmer.ShimmerFrameLayout;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_patient_home, container, false);
        ShimmerFrameLayout shimmerFrameLayout = v.findViewById(R.id.shimmer_patient_home);
        AsyncLayoutInflater layoutInflater = new AsyncLayoutInflater(requireContext());
        shimmerFrameLayout.startShimmerAnimation();
        layoutInflater.inflate(R.layout.layout_symptoms, container, (view, resid, parent) -> {
            shimmerFrameLayout.stopShimmerAnimation();
            shimmerFrameLayout.setVisibility(View.GONE);
            ((ConstraintLayout)v).addView(view);
        });
        return v;
    }
}
