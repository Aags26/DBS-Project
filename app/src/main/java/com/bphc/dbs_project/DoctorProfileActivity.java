package com.bphc.dbs_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bphc.dbs_project.adapters.SlotAdapter;
import com.bphc.dbs_project.app.Constants;
import com.bphc.dbs_project.helper.APIClient;
import com.bphc.dbs_project.helper.ImageHelper;
import com.bphc.dbs_project.helper.Progress;
import com.bphc.dbs_project.helper.Webservices;
import com.bphc.dbs_project.models.Doctor;
import com.bphc.dbs_project.models.ImageResponse;
import com.bphc.dbs_project.models.ServerResponse;
import com.bphc.dbs_project.prefs.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bphc.dbs_project.prefs.SharedPrefsConstants.AUTH;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.ID;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.IMAGE;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.PROFESSION;

public class DoctorProfileActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView textAddProfile;
    private TextView name, hospital, department, phone, email;
    private Webservices webservices;
    private SlotAdapter slotAdapter;

    public final static int IMAGE_PICK = 1001;
    private ProgressDialog progressDialog;
    private ArrayList<String> slots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        profileImage = findViewById(R.id.doctor_profile_image);
        progressDialog = Progress.getProgressDialog(this);

        webservices = APIClient.getRetrofitInstance().create(Webservices.class);
        getDoctorInfo();

        name = findViewById(R.id.text_name);
        hospital = findViewById(R.id.text_hospital);
        department = findViewById(R.id.text_department);
        email = findViewById(R.id.text_email);
        phone = findViewById(R.id.text_phone);

        slots = new ArrayList<>();
        RecyclerView recyclerSlots = findViewById(R.id.recycler_profile_slots);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        slotAdapter = new SlotAdapter(slots);
        recyclerSlots.setLayoutManager(layoutManager);
        recyclerSlots.setAdapter(slotAdapter);

        Button buttonLogout = findViewById(R.id.button_logout);
        buttonLogout.setOnClickListener(v -> {
            if (SharedPrefs.getIntParams(this, AUTH) == 0)
                signOut();
            SharedPrefs.clearPrefsEditor(this);
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        textAddProfile = findViewById(R.id.text_profile_pic);
        textAddProfile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, IMAGE_PICK);
        });

    }

    private void signOut() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(DoctorProfileActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = ImageHelper.getBitmapFromUri(requestCode, data, this);
            profileImage.setImageBitmap(bitmap);
            if (bitmap == null) return;
            String encoded = ImageHelper.getBase64FormBitmap(bitmap);

            createUpload(encoded);

        }
    }

    private void getDoctorInfo() {
        Call<ServerResponse> call = webservices.getDoctorInfo(
                SharedPrefs.getIntParams(this, ID)
        );

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (!serverResponse.isError()) {
                        Doctor doctor = serverResponse.getResult().getDoctor();
                        name.setText(doctor.getName());
                        hospital.setText(doctor.getHospital().getHospitalName());
                        department.setText(doctor.getDepartment().getName());
                        email.setText(doctor.getEmail());
                        phone.setText(doctor.getPhone());

                        Glide.with(DoctorProfileActivity.this).load(doctor.getImage()).into(profileImage);

                        slots.addAll(doctor.getSlots(DoctorProfileActivity.this));
                        slotAdapter.notifyItemRangeInserted(0, slots.size());
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private void updateProfilePic(String image) {
        Call<ServerResponse> call = webservices.updateProfilePic(
                image,
                SharedPrefs.getStringParams(DoctorProfileActivity.this, PROFESSION, ""),
                SharedPrefs.getIntParams(DoctorProfileActivity.this, ID)
        );
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private void createUpload(String encoded) {
        Progress.showProgress(true, "Uploading profile, may take a while....");
        Call<ImageResponse> call = webservices.postImage(Constants.getImgurClientId(), encoded);
        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                ImageResponse mImageResponse = response.body();
                if (mImageResponse != null) {
                    if (mImageResponse.isSuccess()) {
                        ImageResponse.UploadedImage data = mImageResponse.getData();
                        Glide.with(DoctorProfileActivity.this).load(data.link).into(profileImage);
                        SharedPrefs.setStringParams(DoctorProfileActivity.this, IMAGE, data.link);

                        Progress.dismissProgress(progressDialog);

                        updateProfilePic(data.link);
                    }
                }
            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {

            }
        });
    }
}