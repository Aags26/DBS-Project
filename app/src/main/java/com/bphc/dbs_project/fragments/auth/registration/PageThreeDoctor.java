package com.bphc.dbs_project.fragments.auth.registration;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bphc.dbs_project.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class PageThreeDoctor extends Fragment implements View.OnClickListener {

    private TextInputLayout selectHospital, selectDepartment, hospital, department;
    private AutoCompleteTextView textViewHospital, textViewDepartment;
    private ConstraintLayout hospitalLocation;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_three_doctor, container, false);
        selectHospital = view.findViewById(R.id.hospital);
        textViewHospital = view.findViewById(R.id.auto_hospital);
        hospital = view.findViewById(R.id.new_hospital_name);

        hospitalLocation = view.findViewById(R.id.hospital_location);

        selectDepartment = view.findViewById(R.id.department);
        textViewDepartment = view.findViewById(R.id.auto_department);
        department = view.findViewById(R.id.new_department_name);

        ArrayList<String> hospitals = new ArrayList<>();
        hospitals.add("NIL");
        hospitals.add("abcd");
        hospitals.add("efgh");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, hospitals);
        textViewHospital.setAdapter(adapter);

        hospital.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {

    }

    private void checkText(String name){
        if (name.isEmpty())
            hospitalLocation.setVisibility(View.GONE);
        else
            hospitalLocation.setVisibility(View.VISIBLE);
    }
}
