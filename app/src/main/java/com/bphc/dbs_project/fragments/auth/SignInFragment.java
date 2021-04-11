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

import com.bphc.dbs_project.PatientActivity;
import com.bphc.dbs_project.R;
import com.bphc.dbs_project.helper.APIClient;
import com.bphc.dbs_project.helper.Progress;
import com.bphc.dbs_project.helper.Webservices;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.bphc.dbs_project.prefs.SharedPrefsConstants.EMAIL;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.NAME;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.PHONE;

public class SignInFragment extends Fragment implements View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;
    private TextInputLayout inputEmail, inputPassword;
    private String email, password = "-";
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        progressDialog = Progress.getProgressDialog(getContext());

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
        Webservices webservices = APIClient.getRetrofitInstance().create(Webservices.class);

        Call<ServerResponse> call = webservices.loginPatient(email, password);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (!serverResponse.isError()) {
                    Result result = serverResponse.getResult();
                    SharedPrefs.setStringParams(requireContext(), NAME, result.getName());
                    SharedPrefs.setStringParams(requireContext(), EMAIL, result.getEmail());
                    SharedPrefs.setStringParams(requireContext(), PHONE, result.getPhone());

                    //Progress.showProgress(true, "Signing In...");
                    startActivity(new Intent(requireContext(), PatientActivity.class));
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private boolean validateEmail() {
        email = inputEmail.getEditText().getText().toString().trim();
        if(email.isEmpty()) {
            inputEmail.setError("* Please type your email");
            return false;
        } else {
            inputEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        password = inputPassword.getEditText().getText().toString().trim();
        if(password.isEmpty()) {
            inputPassword.setError("* Please type your password");
            return false;
        } else {
            inputPassword.setError(null);
            return true;
        }
    }
}
