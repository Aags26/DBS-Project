package com.bphc.dbs_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bphc.dbs_project.adapters.MedicalRecordAdapter;
import com.bphc.dbs_project.helper.APIClient;
import com.bphc.dbs_project.helper.PdfHelper;
import com.bphc.dbs_project.helper.Webservices;
import com.bphc.dbs_project.models.Appointment;
import com.bphc.dbs_project.models.MedicalRecord;
import com.bphc.dbs_project.models.Patient;
import com.bphc.dbs_project.models.ServerResponse;
import com.bphc.dbs_project.prefs.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.ID;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.NAME;

public class PatientDetailsActivity extends AppCompatActivity {

    private Appointment appointment;
    private MedicalRecordAdapter medicalRecordAdapter;
    private ArrayList<MedicalRecord> medicalRecords;
    private RecyclerView recyclerMedicalRecords;

    private ImageView noRecordsImage;

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);

        Intent intent = getIntent();
        appointment = intent.getParcelableExtra("appointment");
        initUi();

        
        medicalRecords = new ArrayList<>();
        recyclerMedicalRecords = findViewById(R.id.recycler_patient_medical_records);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        medicalRecordAdapter = new MedicalRecordAdapter(medicalRecords, this);
        recyclerMedicalRecords.setLayoutManager(layoutManager);
        recyclerMedicalRecords.setAdapter(medicalRecordAdapter);

        FloatingActionButton buttonAdd = findViewById(R.id.button_add_record);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientDetailsActivity.this, AddMedicalRecordActivity.class);
                intent.putExtra("id", appointment.getPatient().getPId());
                startActivity(intent);
            }
        });

        ImageView generateReportImage = findViewById(R.id.image_generate_report);
        generateReportImage.setOnClickListener(v -> {
            if (checkPermission()) {
                generateReport();
            } else {
                requestPermission();
            }
        });
        
    }
    
    private void initUi() {
        TextView name = findViewById(R.id.book_patient_name);
        name.setText(appointment.getPatient().getName());

        TextView info = findViewById(R.id.book_age_address_name);
        info.setText(appointment.getPatient().getAge() + ", " + appointment.getPatient().getAddress());

        TextView phone = findViewById(R.id.book_patient_phone);
        phone.setText(appointment.getPatient().getPhone());

        TextView email = findViewById(R.id.book_patient_email);
        email.setText(appointment.getPatient().getEmail());

        noRecordsImage = findViewById(R.id.img_no_records);

        ImageView imagePatient = findViewById(R.id.patient_image);
        Glide.with(this).load(appointment.getPatient().getImage()).into(imagePatient);
    }

    @Override
    protected void onStart() {
        super.onStart();
        medicalRecords.clear();
        medicalRecordAdapter.notifyDataSetChanged();
        getPatientMedicalRecords();
    }

    private void getPatientMedicalRecords() {
        Call<ServerResponse> call = APIClient.getRetrofitInstance().create(Webservices.class).getPatientMedicalRecords(
                appointment.getPatient().getPId()
        );

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (!serverResponse.isError()) {
                        medicalRecords.addAll(serverResponse.getResult().getMedicalRecords());
                        medicalRecordAdapter.notifyItemRangeInserted(0, medicalRecords.size());
                        checkEmpty();
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private void generateReport() {

        if (medicalRecords.isEmpty()) {
            Toast.makeText(this, "No medical Records", Toast.LENGTH_SHORT).show();
            return;
        }

        String s = PdfHelper.generateReport(medicalRecords, appointment.getPatient().getName(), this);
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();

    }

    private boolean checkPermission() {

        int permission1 = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                generateReport();
            }
        }
    }

    private void checkEmpty() {
        if (medicalRecords.isEmpty()) {
            noRecordsImage.setVisibility(View.VISIBLE);
            recyclerMedicalRecords.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        } else {
            noRecordsImage.setVisibility(View.GONE);
            recyclerMedicalRecords.setBackgroundColor(Color.parseColor("#EDF1FD"));
        }
    }
}