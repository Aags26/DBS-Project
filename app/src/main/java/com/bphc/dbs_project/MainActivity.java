package com.bphc.dbs_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.bphc.dbs_project.prefs.SharedPrefs;

import static com.bphc.dbs_project.prefs.SharedPrefsConstants.PROFESSION;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.SESSION_FLAG;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}