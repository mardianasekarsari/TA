package com.example.mardiana.alertsystemrumahpompa.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.mardiana.alertsystemrumahpompa.AppConfig;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Mardiana on 3/29/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);

        // Save Token to Shared Preferences
        saveTokenInPref(refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        //sendRegistrationToServer(refreshedToken);
    }

    private void saveTokenInPref(String refreshedToken) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(AppConfig.PREF_FIREBASE, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", refreshedToken);
        editor.commit();
    }

    /*private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }*/
}
