package com.bphc.dbs_project.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySMSBroadcastReceiver extends BroadcastReceiver {

    SmsListener listener;

    public void setListener(SmsListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    if (message != null) {
                        Pattern pattern = Pattern.compile("[0-9]{6}");
                        Matcher matcher = pattern.matcher(message);
                        if (matcher.find()) {
                            String otp = matcher.group();
                            if (listener != null) {
                                listener.onOtpReceived(otp);
                            }
                        }
                    }

                    break;
                case CommonStatusCodes.TIMEOUT:
                    Log.d("TAG", "Timeout");
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    break;
            }
        }
    }

    public interface SmsListener {
        void onOtpReceived(String otp);
    }
}
