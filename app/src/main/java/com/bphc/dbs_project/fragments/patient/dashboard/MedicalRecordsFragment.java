package com.bphc.dbs_project.fragments.patient.dashboard;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bphc.dbs_project.AddMedicalRecordActivity;
import com.bphc.dbs_project.R;
import com.bphc.dbs_project.adapters.MedicalRecordAdapter;
import com.bphc.dbs_project.helper.APIClient;
import com.bphc.dbs_project.helper.OnItemClickListener;
import com.bphc.dbs_project.helper.PdfHelper;
import com.bphc.dbs_project.helper.Webservices;
import com.bphc.dbs_project.models.MedicalRecord;
import com.bphc.dbs_project.models.ServerResponse;
import com.bphc.dbs_project.prefs.SharedPrefs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.ID;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.NAME;

public class MedicalRecordsFragment extends Fragment implements View.OnClickListener {

    private MedicalRecordAdapter medicalRecordAdapter;
    private ArrayList<MedicalRecord> medicalRecords;
    private Webservices webservices;
    private ImageView noRecordsImage;
    private RecyclerView recyclerMedicalRecords;

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_medical_records, container, false);

        webservices = APIClient.getRetrofitInstance().create(Webservices.class);

        medicalRecords = new ArrayList<>();

        noRecordsImage = view.findViewById(R.id.img_no_records);

        recyclerMedicalRecords = view.findViewById(R.id.recycler_patient_medical_records);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        medicalRecordAdapter = new MedicalRecordAdapter(medicalRecords, requireContext());
        recyclerMedicalRecords.setLayoutManager(layoutManager);
        recyclerMedicalRecords.setAdapter(medicalRecordAdapter);

        medicalRecordAdapter.setOnItemClickListener(listener);

        FloatingActionButton addRecord = view.findViewById(R.id.button_add_record);
        addRecord.setOnClickListener(this);

        ImageView generateReportImage = view.findViewById(R.id.image_generate_report);
        generateReportImage.setOnClickListener(v -> {
            if (checkPermission()) {
                generateReport();
            } else {
                requestPermission();
            }
        });

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        medicalRecords.clear();
        medicalRecordAdapter.notifyDataSetChanged();
        getPatientMedicalRecords();
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(requireContext(), AddMedicalRecordActivity.class));
    }

    OnItemClickListener listener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            super.onItemClick(position);
            deletePatientMedicalRecord(position);
        }
    };

    private void getPatientMedicalRecords() {
        Call<ServerResponse> call = webservices.getPatientMedicalRecords(
                SharedPrefs.getIntParams(requireContext(), ID)
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

    private void deletePatientMedicalRecord(int position) {

        Call<ServerResponse> call = webservices.deleteMedicalRecord(
                medicalRecords.get(position).getMid()
        );

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (!serverResponse.isError()) {
                        medicalRecords.remove(position);
                        medicalRecordAdapter.notifyItemRemoved(position);

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
            Toast.makeText(requireContext(), "No medical Records", Toast.LENGTH_SHORT).show();
            return;
        }

        String s = PdfHelper.generateReport(medicalRecords, SharedPrefs.getStringParams(requireContext(), NAME, ""), requireContext());
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show();

    }

    private boolean checkPermission() {

        int permission1 = ContextCompat.checkSelfPermission(requireContext(), WRITE_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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
            recyclerMedicalRecords.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        } else {
            noRecordsImage.setVisibility(View.GONE);
            recyclerMedicalRecords.setBackgroundColor(Color.parseColor("#EDF1FD"));
        }
    }
}
