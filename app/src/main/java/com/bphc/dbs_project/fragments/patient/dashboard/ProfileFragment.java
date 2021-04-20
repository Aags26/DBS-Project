package com.bphc.dbs_project.fragments.patient.dashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bphc.dbs_project.MainActivity;
import com.bphc.dbs_project.R;
import com.bphc.dbs_project.app.Constants;
import com.bphc.dbs_project.helper.APIClient;
import com.bphc.dbs_project.helper.ImageHelper;
import com.bphc.dbs_project.helper.Progress;
import com.bphc.dbs_project.helper.Webservices;
import com.bphc.dbs_project.models.ImageResponse;
import com.bphc.dbs_project.models.Result;
import com.bphc.dbs_project.models.ServerResponse;
import com.bphc.dbs_project.prefs.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.AUTH;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.ID;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.IMAGE;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.PROFESSION;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private CircleImageView profileImage;
    private TextView textAddProfile;
    private TextView name, email, phone, address, age;
    private Webservices webservices;

    public final static int IMAGE_PICK = 1001;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_profile, container, false);

        profileImage = view.findViewById(R.id.patient_profile_image);
        textAddProfile = view.findViewById(R.id.text_profile_pic);

        progressDialog = Progress.getProgressDialog(getContext());

        webservices = APIClient.getRetrofitInstance().create(Webservices.class);
        getPatientInfo();

        name = view.findViewById(R.id.text_name);
        email = view.findViewById(R.id.text_email);
        phone = view.findViewById(R.id.text_phone);
        address = view.findViewById(R.id.text_address);
        age = view.findViewById(R.id.text_age);

        textAddProfile.setOnClickListener(this);

        Button buttonLogout = view.findViewById(R.id.button_logout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPrefs.getIntParams(requireContext(), AUTH) == 0)
                    signOut();
                SharedPrefs.clearPrefsEditor(requireContext());
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    private void signOut() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(requireContext(), "Signed out", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = ImageHelper.getBitmapFromUri(requestCode, data, requireContext());
            profileImage.setImageBitmap(bitmap);
            if (bitmap == null) return;
            String encoded = ImageHelper.getBase64FormBitmap(bitmap);

            createUpload(encoded);

        }
    }

    private void getPatientInfo() {
        Call<ServerResponse> call = webservices.getPatientInfo(
                SharedPrefs.getIntParams(requireContext(), ID)
        );
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (!serverResponse.isError()) {
                        Result result = serverResponse.getResult();

                        Glide.with(requireContext()).load(result.getPatient().getImage()).into(profileImage);

                        name.setText(result.getPatient().getName());
                        email.setText(result.getPatient().getEmail());
                        phone.setText(result.getPatient().getPhone());
                        address.setText(result.getPatient().getAddress());
                        age.setText(result.getPatient().getAge() + "");

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
                SharedPrefs.getStringParams(requireContext(), PROFESSION, ""),
                SharedPrefs.getIntParams(requireContext(), ID)
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
                        Glide.with(requireContext()).load(data.link).into(profileImage);
                        SharedPrefs.setStringParams(requireContext(), IMAGE, data.link);

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
