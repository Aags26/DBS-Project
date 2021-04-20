package com.bphc.dbs_project.fragments.auth.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bphc.dbs_project.R;
import com.bphc.dbs_project.helper.APIClient;
import com.bphc.dbs_project.helper.Progress;
import com.bphc.dbs_project.helper.Webservices;
import com.bphc.dbs_project.models.ServerResponse;
import com.bphc.dbs_project.prefs.SharedPrefs;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bphc.dbs_project.prefs.SharedPrefsConstants.AUTH;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.EMAIL;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.NAME;
import static com.bphc.dbs_project.prefs.SharedPrefsConstants.PASSWORD;

public class PageOne extends Fragment implements View.OnClickListener {


    private static final int RC_SIGN_UP = 9001;
    private TextInputLayout inputName, inputEmail, inputPassword;
    private String name, email, password = "";
    private GoogleSignInClient mGoogleSignInClient;
    private int auth;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_one, container, false);

        progressDialog = Progress.getProgressDialog(getContext());

        inputName = view.findViewById(R.id.user_name_sign_up);
        inputEmail = view.findViewById(R.id.user_email_sign_up);
        inputPassword = view.findViewById(R.id.user_password_sign_up);

        ImageView googleSignUp = view.findViewById(R.id.google_auth_sign_up);
        googleSignUp.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        FloatingActionButton proceedButton = view.findViewById(R.id.button_next);
        proceedButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.google_auth_sign_up) {
            signUpWithGoogle();
        } else {
            customSignUp();
        }
    }

    private void signUpWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_UP);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_UP && resultCode == -1) {
            Progress.showProgress(true, "Signing In...");
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

        name = account.getDisplayName();
        email = account.getEmail();
        auth = 0;

        checkCredentials();
    }

    private void customSignUp() {
        if (!validateName() | !validateEmail() | !validatePassword())
            return;
        checkCredentials();
        auth = 1;
    }

    private void proceed() {

        SharedPrefs.setStringParams(requireContext(), NAME, name);
        SharedPrefs.setStringParams(requireContext(), EMAIL, email);
        SharedPrefs.setStringParams(requireContext(), PASSWORD, password);
        SharedPrefs.setIntParams(requireContext(), AUTH, auth);

        Progress.dismissProgress(progressDialog);

        final NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_pageOne_to_pageTwo);
    }

    private void checkCredentials() {
        Webservices webservices = APIClient.getRetrofitInstance().create(Webservices.class);
        Call<ServerResponse> call = webservices.checkCredentials(
                email,
                password,
                auth
        );

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.isError()) {
                        inputEmail.setError("This email already exists");
                        inputPassword.setError("This password already exists");
                    } else {
                        inputEmail.setError(null);
                        inputPassword.setError(null);
                        proceed();
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private boolean validateName() {
        name = inputName.getEditText().getText().toString().trim();
        if(name.isEmpty()) {
            inputName.setError("* Please type your name");
            return false;
        } else {
            inputName.setError(null);
            return true;
        }
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
