package com.bphc.dbs_project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bphc.dbs_project.adapters.SlotAdapter;
import com.bphc.dbs_project.helper.APIClient;
import com.bphc.dbs_project.helper.OnItemClickListener;
import com.bphc.dbs_project.helper.Webservices;
import com.bphc.dbs_project.models.Doctor;
import com.bphc.dbs_project.models.ServerResponse;
import com.bphc.dbs_project.prefs.SharedPrefs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bphc.dbs_project.prefs.SharedPrefsConstants.ID;

public class BookAppointmentActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView doctorName, doctorHospDept, doctorPhone, doctorEmail, todaySlots;
    private TextView[] otherSlots = new TextView[3];
    private RecyclerView recyclerTodaySlots, recyclerOtherDaySlots;
    private SlotAdapter todaySlotAdapter, otherDaySlotAdapter;
    private TextView prevView = null, curView = null;
    private AlertDialog alertDialog;
    private ArrayList<String> availableSlotsToday, availableSlotsDate1, availableSlotsDate2, availableSlotsDate3, otherDaySlots;
    private String todayDate;
    private Doctor doctor;
    private Webservices webservices;
    private TextView textNoSlots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        initUi();

        Intent intent = getIntent();
        doctor = intent.getParcelableExtra("doctor");

        webservices = APIClient.getRetrofitInstance().create(Webservices.class);
        getAvailableAppointments();

        doctorName.setText(doctor.getName());
        doctorHospDept.setText(doctor.getDepartment().getName() + " at " + doctor.getHospital().getHospitalName() + " hospital");
        doctorPhone.setText(doctor.getPhone());
        doctorEmail.setText(doctor.getEmail());

        textNoSlots = findViewById(R.id.text_no_slots);

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
        todayDate = df.format(c);
        todaySlots.setText("Today, " + todayDate);

        availableSlotsToday = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        todaySlotAdapter = new SlotAdapter(availableSlotsToday);
        recyclerTodaySlots.setLayoutManager(layoutManager);
        recyclerTodaySlots.setAdapter(todaySlotAdapter);


        SimpleDateFormat df2 = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());

        for (int i = 0; i < 3; i++) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(c);
            cal.add(Calendar.DAY_OF_MONTH, i + 1);
            Date date = cal.getTime();
            otherSlots[i].setText(df2.format(date));
        }

        todaySlotAdapter.setOnItemClickListener(listener);

    }

    private void initUi() {
        doctorName = findViewById(R.id.book_doctor_name);
        doctorHospDept = findViewById(R.id.book_dept_hosp_name);
        doctorPhone = findViewById(R.id.book_doctor_phone);
        doctorEmail = findViewById(R.id.book_doctor_email);

        todaySlots = findViewById(R.id.text_slots_today);
        recyclerTodaySlots = findViewById(R.id.recycler_slots_today);

        for (int i = 0; i < 3; i++) {
            String textId = "text_day_" + i;
            int resId = getResources().getIdentifier(textId, "id", getPackageName());
            otherSlots[i] = findViewById(resId);
            otherSlots[i].setOnClickListener(this);
        }
        recyclerOtherDaySlots = findViewById(R.id.recycler_other_days);

    }

    OnItemClickListener listener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            super.onItemClick(position);
            if (curView == null) {
                checkClash(availableSlotsToday.get(position), todayDate);
            } else {
                checkClash(otherDaySlots.get(position), curView.getText().toString());
            }
        }

        @Override
        public void onItemClick(int position, int flag) {
            super.onItemClick(position, flag);
        }
    };

    @Override
    public void onClick(View v) {
        curView = (TextView) v;
        curView.setTextColor(getResources().getColor(R.color.purple_500));
        curView.setElegantTextHeight(true);
        if (prevView != null) {
            prevView.setTextColor(getResources().getColor(R.color.black));
            prevView.setElegantTextHeight(false);
        }
        prevView = curView;
        otherDaySlots = new ArrayList<>();
        if (curView.getText().toString().equals(otherSlots[0].getText().toString()))
            otherDaySlots.addAll(availableSlotsDate1);
        if (curView.getText().toString().equals(otherSlots[1].getText().toString()))
            otherDaySlots.addAll(availableSlotsDate2);
        if (curView.getText().toString().equals(otherSlots[2].getText().toString()))
            otherDaySlots.addAll(availableSlotsDate3);


        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        otherDaySlotAdapter = new SlotAdapter(otherDaySlots);
        recyclerOtherDaySlots.setLayoutManager(layoutManager);
        recyclerOtherDaySlots.setAdapter(otherDaySlotAdapter);
        otherDaySlotAdapter.notifyDataSetChanged();

        otherDaySlotAdapter.setOnItemClickListener(listener);

    }

    private void getAvailableAppointments() {
        Call<ServerResponse> call = webservices.getAvailableAppointments(
                doctor.getDocId()
        );
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                availableSlotsDate1 = new ArrayList<>();
                availableSlotsDate2 = new ArrayList<>();
                availableSlotsDate3 = new ArrayList<>();
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (!serverResponse.isError()) {
                        availableSlotsToday.addAll(serverResponse.getResult().getTodaySlots(BookAppointmentActivity.this));
                        availableSlotsDate1.addAll(serverResponse.getResult().getDate1Slots(BookAppointmentActivity.this));
                        availableSlotsDate2.addAll(serverResponse.getResult().getDate2Slots(BookAppointmentActivity.this));
                        availableSlotsDate3.addAll(serverResponse.getResult().getDate3Slots(BookAppointmentActivity.this));

                        todaySlotAdapter.notifyDataSetChanged();
                        checkEmpty();
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private void checkClash(String slot, String date) {
        Call<ServerResponse> call = webservices.checkClash(
                SharedPrefs.getIntParams(this, ID),
                getTime(slot, date)
        );

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (!serverResponse.isError()) {
                        inflateDialog(slot, date);
                    } else {
                        Toast.makeText(BookAppointmentActivity.this, "You already have an appointment at this time", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private void inflateDialog(String slot, String date) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.dialog_confirm_booking, null);
        builder.setView(view);

        TextView textBookingTime = view.findViewById(R.id.text_booking_time);
        textBookingTime.setText(date + " from " + slot);

        Button buttonConfirm = view.findViewById(R.id.button_confirm_booking);
        Button buttonClose = view.findViewById(R.id.button_close_booking);

        alertDialog = builder.create();
        alertDialog.show();

        buttonConfirm.setOnClickListener(v -> bookAppointment(slot, date));
        buttonClose.setOnClickListener(v -> alertDialog.dismiss());

    }

    private void bookAppointment(String slot, String date) {

        Call<ServerResponse> call = webservices.bookAppointment(
                SharedPrefs.getIntParams(this, ID),
                doctor.getDocId(),
                Integer.parseInt(slot.split("-")[0].replaceAll("[^0-9]", "")),
                getTime(slot, date)
        );

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                Toast.makeText(BookAppointmentActivity.this, "Booking Confirmed", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private String getTime(String slot, String date) {
        int t = Integer.parseInt(slot.split("-")[0].replaceAll("[^0-9]", ""));
        String s = "", time = "";
        if (t >= 1 && t <= 6)
            s = (t + 12) + ":00:00";
        else
            s = t + ":00:00";

        SimpleDateFormat sdf1 = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM", Locale.getDefault());
        try {
            time = sdf2.format(sdf1.parse(date));
            Log.i("TAG", time + "-2021" + " " + s);
        } catch (Exception e) {

        }
        return time + "-2021" + " " + s;
    }

    private void checkEmpty() {
        if (availableSlotsToday.isEmpty()) {
            textNoSlots.setVisibility(View.VISIBLE);
        } else {
            textNoSlots.setVisibility(View.GONE);
        }
    }
}