package com.bphc.dbs_project.fragments.auth;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bphc.dbs_project.DoctorActivity;
import com.bphc.dbs_project.PatientActivity;
import com.bphc.dbs_project.R;
import com.bphc.dbs_project.helper.APIClient;
import com.bphc.dbs_project.helper.Progress;
import com.bphc.dbs_project.helper.Webservices;
import com.bphc.dbs_project.models.Doctor;
import com.bphc.dbs_project.models.Patient;
import com.bphc.dbs_project.models.Result;
import com.bphc.dbs_project.models.ServerResponse;
import com.bphc.dbs_project.prefs.SharedPrefs;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.bphc.dbs_project.prefs.SharedPrefsConstants.ADDRESS;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.AGE;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.DEPARTMENT;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.EMAIL;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.HOSPITAL;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.ID;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.NAME;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.PHONE;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.PROFESSION;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.SESSION_FLAG;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.SLOTS;

public class SignInFragment extends Fragment implements View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;
    private TextInputLayout inputEmail, inputPassword;
    private String email, password = "";
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        progressDialog = Progress.getProgressDialog(getContext());

        if (SharedPrefs.getIntParams(requireContext(), SESSION_FLAG) == 1) {
            Progress.showProgress(true, "Signing In...");
            if (SharedPrefs.getStringParams(requireContext(), PROFESSION, "").equals("patient"))
                startActivity(new Intent(requireContext(), PatientActivity.class));
            else
                startActivity(new Intent(requireContext(), DoctorActivity.class));
            Toast.makeText(requireContext(), "Signed in as " + SharedPrefs.getStringParams(requireContext(), NAME, ""), Toast.LENGTH_SHORT).show();
            Progress.dismissProgress(progressDialog);
            requireActivity().finish();
        }

        inputEmail = view.findViewById(R.id.user_email_sign_in);
        inputPassword = view.findViewById(R.id.user_password_sign_in);

        Button customSignInButton = view.findViewById(R.id.custom_sign_in_button);
        customSignInButton.setOnClickListener(this);

        ImageView googleSignIn = view.findViewById(R.id.google_auth_sign_in);
        googleSignIn.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        TextView signUpText = view.findViewById(R.id.user_sign_up_text);
        signUpText.setOnClickListener(this);

        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_auth_sign_in:
                signInWithGoogle();
                break;
            case R.id.custom_sign_in_button:
                customSignIn();
                break;
            case R.id.user_sign_up_text:
                final NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_signInFragment_to_pageOne);
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN && resultCode == -1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        email = account.getEmail();
        checkCredentials();
    }

    private void customSignIn() {
        if (!validateEmail() | !validatePassword())
            return;

        checkCredentials();

    }

    private void checkCredentials() {
        Progress.showProgress(true, "Signing In...");
        Webservices webservices = APIClient.getRetrofitInstance().create(Webservices.class);

        Call<ServerResponse> call = webservices.loginPatient(email, password);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (!serverResponse.isError()) {
                    Result result = serverResponse.getResult();
                    SharedPrefs.setIntParams(requireContext(), SESSION_FLAG, 1);
                    if (result.getProfession().equals("patient")) {
                        Patient patient = result.getPatient();
                        SharedPrefs.setStringParams(requireContext(), PROFESSION, "patient");
                        SharedPrefs.setStringParams(requireContext(), NAME, patient.getName());
                        SharedPrefs.setStringParams(requireContext(), EMAIL, patient.getEmail());
                        SharedPrefs.setStringParams(requireContext(), ADDRESS, patient.getAddress());
                        SharedPrefs.setStringParams(requireContext(), PHONE, patient.getPhone());
                        SharedPrefs.setIntParams(requireContext(), AGE, patient.getAge());
                        SharedPrefs.setIntParams(requireContext(), ID, patient.getPId());
                        startActivity(new Intent(requireContext(), PatientActivity.class));
                    } else {
                        Doctor doctor = result.getDoctor();
                        SharedPrefs.setStringParams(requireContext(), PROFESSION, "doctor");
                        SharedPrefs.setStringParams(requireContext(), NAME, doctor.getName());
                        SharedPrefs.setStringParams(requireContext(), EMAIL, doctor.getEmail());
                        SharedPrefs.setStringParams(requireContext(), PHONE, doctor.getPhone());
                        SharedPrefs.setIntParams(requireContext(), ID, doctor.getDocId());
                        SharedPrefs.setStringParams(requireContext(), HOSPITAL, doctor.getHospital().getHospitalName());
                        SharedPrefs.setStringParams(requireContext(), DEPARTMENT, doctor.getDepartment().getName());

                        Gson gson = new Gson();
                        SharedPrefs.setStringParams(requireContext(), SLOTS, gson.toJson(doctor.getSlots(requireContext())));

                        startActivity(new Intent(requireContext(), DoctorActivity.class));
                    }
                    Toast.makeText(requireContext(), "Signed in as " + SharedPrefs.getStringParams(requireContext(), NAME, ""), Toast.LENGTH_SHORT).show();
                    requireActivity().finish();
                } else {
                    Toast.makeText(requireContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
                Progress.dismissProgress(progressDialog);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private boolean validateEmail() {
        email = inputEmail.getEditText().getText().toString().trim();
        if (email.isEmpty()) {
            inputEmail.setError("* Please type your email");
            return false;
        } else {
            inputEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        password = inputPassword.getEditText().getText().toString().trim();
        if (password.isEmpty()) {
            inputPassword.setError("* Please type your password");
            return false;
        } else {
            inputPassword.setError(null);
            return true;
        }
    }
}
