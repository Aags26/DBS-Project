package com.bphc.dbs_project.fragments.auth.registration;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bphc.dbs_project.R;
import com.bphc.dbs_project.helper.APIClient;
import com.bphc.dbs_project.helper.Webservices;
import com.bphc.dbs_project.models.Department;
import com.bphc.dbs_project.models.Hospital;
import com.bphc.dbs_project.models.Result;
import com.bphc.dbs_project.models.ServerResponse;
import com.bphc.dbs_project.prefs.SharedPrefs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bphc.dbs_project.prefs.SharedPrefsConstants.DEPARTMENT;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.HOSPITAL;

public class PageThreeDoctor extends Fragment implements View.OnClickListener {

    private TextInputLayout selectHospital, selectDepartment, hospital, department;
    private AutoCompleteTextView textViewHospital, textViewDepartment;
    private ConstraintLayout hospitalLocation;
    private Webservices webservices;
    private ArrayList<String> hospitals = new ArrayList<>();
    private ArrayList<String> departments = new ArrayList<>();
    private EditText street, area;
    private String inputStreet, inputArea;
    private FloatingActionButton buttonProceed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_three_doctor, container, false);

        initUi(view);

        webservices = APIClient.getRetrofitInstance().create(Webservices.class);

        getHospitals();
        getDepartments();

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

        buttonProceed.setOnClickListener(this);

        return view;
    }

    private void initUi(View view) {
        selectHospital = view.findViewById(R.id.hospital);
        textViewHospital = view.findViewById(R.id.auto_hospital);
        hospital = view.findViewById(R.id.new_hospital_name);
        hospitalLocation = view.findViewById(R.id.hospital_location);

        selectDepartment = view.findViewById(R.id.department);
        textViewDepartment = view.findViewById(R.id.auto_department);
        department = view.findViewById(R.id.new_department_name);

        street = view.findViewById(R.id.street);
        area = view.findViewById(R.id.area);

        buttonProceed = view.findViewById(R.id.button_next);

    }

    private void getHospitals() {
        Call<ServerResponse> call = webservices.getHospitals();
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    Result result = serverResponse.getResult();
                    hospitals.clear();
                    for (Hospital hospital : result.getHospitals())
                        hospitals.add(hospital.getHospitalName());
                    ArrayAdapter<String> hospitalAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, hospitals);
                    textViewHospital.setAdapter(hospitalAdapter);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private void getDepartments() {
        Call<ServerResponse> call = webservices.getDepartments();
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    Result result = serverResponse.getResult();
                    departments.clear();
                    for (Department department : result.getDepartments())
                        departments.add(department.getName());
                    ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, departments);
                    textViewDepartment.setAdapter(departmentAdapter);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private void registerHospital() {
        if (!validateArea() | !validateStreet())
            return;
        Call<ServerResponse> call = webservices.registerHospital(
                hospital.getEditText().getText().toString().trim(),
                inputStreet,
                inputArea
        );
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {

                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    Result result = serverResponse.getResult();

                    SharedPrefs.setStringParams(requireContext(), HOSPITAL, result.getHospital().getHospitalName());
                    final NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.action_pageThreeDoctor_to_pageFourDoctor);

                }

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        if (textViewDepartment.getText().toString().isEmpty() && !department.getEditText().getText().toString().isEmpty())
            SharedPrefs.setStringParams(requireContext(), DEPARTMENT, department.getEditText().getText().toString());
        else if (!textViewDepartment.getText().toString().isEmpty() && department.getEditText().getText().toString().isEmpty())
            SharedPrefs.setStringParams(requireContext(), DEPARTMENT, textViewDepartment.getText().toString());
        else if (!textViewDepartment.getText().toString().isEmpty() && !department.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Just fill one department", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(requireContext(), "Fill in department name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!hospital.getEditText().getText().toString().trim().isEmpty() && textViewHospital.getText().toString().isEmpty())
            registerHospital();
        else if (hospital.getEditText().getText().toString().trim().isEmpty() && !textViewHospital.getText().toString().isEmpty()) {
            SharedPrefs.setStringParams(requireContext(), HOSPITAL, textViewHospital.getText().toString());
        } else if (!hospital.getEditText().getText().toString().trim().isEmpty() && !textViewHospital.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Just fill one hospital", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Fill in hospital name", Toast.LENGTH_SHORT).show();
        }

        checkHospDept();

    }

    private void checkHospDept() {
        Call<ServerResponse> call = webservices.checkHospDept(
                SharedPrefs.getStringParams(requireContext(), HOSPITAL, ""),
                SharedPrefs.getStringParams(requireContext(), DEPARTMENT, "")
        );

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (!serverResponse.isError()) {
                        final NavController navController = Navigation.findNavController(requireView());
                        navController.navigate(R.id.action_pageThreeDoctor_to_pageFourDoctor);
                    } else {
                        SharedPrefs.removeKey(requireContext(), HOSPITAL);
                        SharedPrefs.removeKey(requireContext(), DEPARTMENT);
                        Toast.makeText(requireContext(), "A doctor already exists for this hospital and department", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private void checkText(String name) {
        if (name.isEmpty()) {
            hospitalLocation.setVisibility(View.GONE);
            street.setText("");
            area.setText("");
        } else
            hospitalLocation.setVisibility(View.VISIBLE);
    }

    private boolean validateStreet() {
        inputStreet = street.getText().toString().trim();
        if (inputStreet.isEmpty()) {
            street.setError("* Please enter street name");
            return false;
        } else {
            street.setError(null);
            return true;
        }
    }

    private boolean validateArea() {
        inputArea = area.getText().toString().trim();
        if (inputArea.isEmpty()) {
            area.setError("* Please enter the area");
            return false;
        } else {
            area.setError(null);
            return true;
        }
    }

}
