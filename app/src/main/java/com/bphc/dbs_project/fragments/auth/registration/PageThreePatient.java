package com.bphc.dbs_project.fragments.auth.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bphc.dbs_project.R;
import com.google.android.material.textfield.TextInputLayout;

public class PageThreePatient extends Fragment implements View.OnClickListener {

    private TextInputLayout inputAge, inputAddress;
    private String age, address;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_three_patient, container, false);
        inputAge = view.findViewById(R.id.age);
        inputAddress = view.findViewById(R.id.address);

        Button buttonSubmit = view.findViewById(R.id.button_submit);
        buttonSubmit.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (!validateAge() | !validateAddress())
            return;
        registerPatient();
    }

    private void registerPatient() {



    }

    private boolean validateAge() {
        age = inputAge.getEditText().getText().toString().trim();
        if (age.isEmpty()) {
            inputAge.setError("* Please enter your age");
            return false;
        } else {
            inputAge.setError(null);
            return true;
        }
    }

    private boolean validateAddress() {
        address = inputAddress.getEditText().getText().toString().trim();
        if (address.isEmpty()) {
            inputAddress.setError("* Please enter your address");
            return false;
        } else {
            inputAddress.setError(null);
            return true;
        }
    }
}
