package com.example.imagepro;

import android.telephony.SmsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class EmergencyAssistanceHelper {

    public static void sendEmergencyMessage(Context context, String contactNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(contactNumber, null, message, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendEmergencyMessageViaIntent(Context context, String contactNumber, String message) {
        try {
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setData(Uri.parse("smsto:" + contactNumber));
            smsIntent.putExtra("sms_body", message);
            context.startActivity(smsIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
