package com.bphc.dbs_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.bphc.dbs_project.helper.APIClient;
import com.bphc.dbs_project.helper.Webservices;
import com.bphc.dbs_project.models.ServerResponse;
import com.bphc.dbs_project.prefs.SharedPrefs;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bphc.dbs_project.prefs.SharedPrefsConstants.ID;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.NAME;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.PROFESSION;

public class AddMedicalRecordActivity extends AppCompatActivity {

    private TextInputLayout inputDiagnosis, inputDOE, inputDoctor, inputDescription;
    private String diagnosis, DOE, doctor, description;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medical_record);

        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        if (id == -1)
            id = SharedPrefs.getIntParams(this, ID);

        inputDiagnosis = findViewById(R.id.patient_diagnosis);
        inputDOE = findViewById(R.id.patient_diagnosis_date);
        inputDoctor = findViewById(R.id.diagnosis_doctor);

        if (SharedPrefs.getStringParams(this, PROFESSION, "").equals("patient"))
            inputDoctor.setVisibility(View.VISIBLE);
        else
            inputDoctor.setVisibility(View.GONE);

        inputDescription = findViewById(R.id.patient_diagnosis_description);

        ImageView calendarImage = findViewById(R.id.image_date_picker);

        final Calendar calendar = Calendar.getInstance();
        calendarImage.setOnClickListener(v -> {
            int y = calendar.get(Calendar.YEAR);
            int m = calendar.get(Calendar.MONTH);
            int d = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(AddMedicalRecordActivity.this, (view, year, month, dayOfMonth) -> {
                String date = dayOfMonth + "-" + (month + 1) + "-" + year;
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd-M-yyyy", Locale.getDefault());
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

                try {
                    inputDOE.getEditText().setText(sdf2.format(sdf1.parse(date)));
                } catch (Exception e) {

                }

            }, y, m, d);
            datePickerDialog.show();
        });

        Button buttonCreateRecord = findViewById(R.id.button_create_records);
        buttonCreateRecord.setOnClickListener(v -> {
            if (SharedPrefs.getStringParams(this, PROFESSION, "").equals("patient")) {
                if (!validateDiagnosis() | !validateDOE() | !validateDoctor() | !validateDescription())
                    return;
            } else {
                if (!validateDiagnosis() | !validateDOE() | !validateDescription())
                    return;
            }

            createMedicalRecord();
        });

    }

    private void createMedicalRecord() {
        if (SharedPrefs.getStringParams(this, PROFESSION, "").equals("doctor"))
            doctor = SharedPrefs.getStringParams(this, NAME, "");
        description = doctor + "|" + description;
        Webservices webservices = APIClient.getRetrofitInstance().create(Webservices.class);
        Call<ServerResponse> call = webservices.createMedicalRecord(
                id,
                DOE,
                " ",
                diagnosis,
                description
        );

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (!serverResponse.isError()) {
                        Toast.makeText(AddMedicalRecordActivity.this, "Added a record", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private boolean validateDiagnosis() {
        diagnosis = inputDiagnosis.getEditText().getText().toString().trim();
        if (diagnosis.isEmpty()) {
            inputDiagnosis.setError("* Please enter the diagnosis");
            return false;
        } else {
            inputDiagnosis.setError(null);
            return true;
        }
    }

    private boolean validateDOE() {
        DOE = inputDOE.getEditText().getText().toString().trim();
        if (DOE.isEmpty()) {
            inputDOE.setError("* Please enter the date of examination");
            return false;
        } else {
            inputDOE.setError(null);
            return true;
        }
    }

    private boolean validateDoctor() {
        doctor = inputDoctor.getEditText().getText().toString().trim();
        if (doctor.isEmpty()) {
            inputDoctor.setError("* Please enter the name of doctor");
            return false;
        } else {
            inputDoctor.setError(null);
            return true;
        }
    }

    private boolean validateDescription() {
        description = inputDescription.getEditText().getText().toString().trim();
        if (description.isEmpty()) {
            inputDescription.setError("* Please enter the description");
            return false;
        } else {
            inputDescription.setError(null);
            return true;
        }
    }
}