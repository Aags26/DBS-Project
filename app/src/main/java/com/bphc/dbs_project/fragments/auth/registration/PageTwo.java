package com.bphc.dbs_project.fragments.auth.registration;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bphc.dbs_project.R;
import com.bphc.dbs_project.helper.APIClient;
import com.bphc.dbs_project.helper.AppSignatureHashHelper;
import com.bphc.dbs_project.helper.MySMSBroadcastReceiver;
import com.bphc.dbs_project.helper.Webservices;
import com.bphc.dbs_project.models.ServerResponse;
import com.bphc.dbs_project.prefs.SharedPrefs;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bphc.dbs_project.prefs.SharedPrefsConstants.PHONE;

public class PageTwo extends Fragment implements View.OnClickListener, MySMSBroadcastReceiver.SmsListener {

    private TextInputLayout inputPhone, inputOtp;
    private Button button_getOtp;
    private TextView resendOtp;
    private String phone, otp, regex;
    private final int RESOLVE_HINT = 7;
    private CredentialsClient mCredentialsClient;
    private Webservices webservices;
    private MySMSBroadcastReceiver smsReceiver;

    private RadioGroup radioGroup;
    private String hash;
    private boolean isVerified = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_two, container, false);

        webservices = APIClient.getRetrofitInstance().create(Webservices.class);

        inputPhone = view.findViewById(R.id.phone_number);
        inputOtp = view.findViewById(R.id.layout_text_otp);

        radioGroup = view.findViewById(R.id.radioGroup);

        regex = "[0-9]{10}";
        inputPhone.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkRegex(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        button_getOtp = view.findViewById(R.id.button_getOtp);
        button_getOtp.setOnClickListener(this);

        resendOtp = view.findViewById(R.id.resend_otp);
        resendOtp.setOnClickListener(this);

        FloatingActionButton buttonNext = view.findViewById(R.id.button_next);
        buttonNext.setOnClickListener(this);

        mCredentialsClient = Credentials.getClient(requireContext());

        AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(getContext());
        hash = appSignatureHashHelper.getAppSignatures().get(0);
        Log.d("TAG", "HashKey: " + appSignatureHashHelper.getAppSignatures().get(0));

        pickPhoneNumber();
        startSmsRetriever();

        return view;
    }

    private void pickPhoneNumber() {

        HintRequest hintRequest = new HintRequest.Builder()
                .setHintPickerConfig(new CredentialPickerConfig.Builder()
                        .setShowCancelButton(true)
                        .build())
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent pendingIntent = mCredentialsClient.getHintPickerIntent(hintRequest);

        try {
            startIntentSenderForResult(pendingIntent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0, null);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == Activity.RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                String phone_number = credential.getId();
                inputPhone.getEditText().setText(phone_number.substring(phone_number.length() - 10));
            }
        }
    }

    private void startSmsRetriever() {

        smsReceiver = new MySMSBroadcastReceiver();
        smsReceiver.setListener(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        getActivity().registerReceiver(smsReceiver, intentFilter);

        SmsRetrieverClient client = SmsRetriever.getClient(getContext());
        Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(aVoid -> {

        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to start retrieving", Toast.LENGTH_SHORT).show());

    }

    @Override
    public void onOtpReceived(String otp) {

        inputOtp.getEditText().setText(otp);

        if (smsReceiver != null) {
            getActivity().unregisterReceiver(smsReceiver);
            smsReceiver = null;
        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_getOtp:
                if (button_getOtp.getText().toString().equalsIgnoreCase("get otp")) {
                    if (!validatePhoneNumber())
                        return;
                    inputOtp.setVisibility(View.VISIBLE);
                    resendOtp.setVisibility(View.VISIBLE);
                    button_getOtp.setText("Verify");
                    requestOtp();
                } else {
                    if (!validateOtp())
                        return;
                    verifyOtp();
                }

                break;
            case R.id.resend_otp:
                break;
            case R.id.button_next:
                if (!isVerified) {
                    inputPhone.setError("* Verify your phone number first");
                    return;
                }
                else
                    inputPhone.setError(null);
                int radioId =radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = requireView().findViewById(radioId);
                String profession = radioButton.getText().toString();
                final NavController navController = Navigation.findNavController(requireView());
                if (profession.equals("Patient"))
                    navController.navigate(R.id.action_pageTwo_to_pageThreePatient);
                else
                    navController.navigate(R.id.action_pageTwo_to_pageThreeDoctor);
        }
    }

    private void requestOtp() {
        Call<ServerResponse> call = webservices.getOtp(
                phone
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

    private void verifyOtp() {
        Call<ServerResponse> call = webservices.verifyOtp(
                phone,
                otp
        );
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (!serverResponse.isError()) {
                        Toast.makeText(requireContext(), "Phone Number Verified", Toast.LENGTH_SHORT).show();
                        SharedPrefs.setStringParams(requireContext(), PHONE, phone);
                        isVerified = true;
                        inputOtp.setVisibility(View.GONE);
                        resendOtp.setVisibility(View.GONE);
                        button_getOtp.setVisibility(View.GONE);

                    } else {
                        Toast.makeText(requireContext(), "OTP is incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private boolean validatePhoneNumber() {
        phone = inputPhone.getEditText().getText().toString().trim();
        if (phone.isEmpty()) {
            inputPhone.setError("* Please type your mobile number");
            return false;
        } else {
            if (phone.length() != 10) {
                inputPhone.setError("* It should be a 10-digit number");
                return false;
            } else {
                inputPhone.setError(null);
                return true;
            }
        }
    }

    private boolean validateOtp() {
        otp = inputOtp.getEditText().getText().toString().trim();
        if (otp.isEmpty()) {
            inputOtp.setError("* Please type your OTP");
            return false;
        } else {
            if (otp.length() != 6) {
                inputOtp.setError("* It should be a 6-digit number");
                return false;
            } else {
                inputOtp.setError(null);
                return true;
            }
        }
    }

    private void checkRegex(CharSequence text) {
        if (Pattern.matches(regex, text)) {
            inputPhone.setError(null);
        } else {
            inputPhone.setError("* It should be a 10-digit number");
        }
    }
}
