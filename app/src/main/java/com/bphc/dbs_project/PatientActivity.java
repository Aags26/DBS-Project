package com.bphc.dbs_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bphc.dbs_project.fragments.patient.dashboard.AppointmentsFragment;
import com.bphc.dbs_project.fragments.patient.dashboard.HomeFragment;
import com.bphc.dbs_project.fragments.patient.dashboard.MedicalRecordsFragment;
import com.bphc.dbs_project.fragments.patient.dashboard.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PatientActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(this);
        loadFragment(new HomeFragment());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        switch (item.getItemId()) {

            case R.id.main:
                selectedFragment = new HomeFragment();
                break;
            case R.id.appointments:
                selectedFragment = new AppointmentsFragment();
                break;
            case R.id.medical_records:
                selectedFragment = new MedicalRecordsFragment();
                break;
            case R.id.profile:
                selectedFragment = new ProfileFragment();
        }


        loadFragment(selectedFragment);
        return true;
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}