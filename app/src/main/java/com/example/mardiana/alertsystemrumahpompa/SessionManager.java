package com.example.mardiana.alertsystemrumahpompa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mardiana on 2/17/2017.
 */

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static String TAG = SessionManager.class.getSimpleName();
    private static final String PREF_NAME = "AlertSystemLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_NAME = "nama_user";
    public static final String KEY_ADDRESS = "alamat_user";
    public static final String KEY_ROLE = "role_user";
    public static final String KEY_PHONE = "no_telp_user";
    public static final String KEY_RUMAHPOMPAID = "rumah_pompa_user";
    public static final String KEY_PASSWORD = "password";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void setLoginSession(String username, String nama, String role, String alamat, String telepon, String rumah_pompa, String password) {

        editor.putBoolean(KEY_IS_LOGGEDIN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_NAME, nama);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_ADDRESS, alamat);
        editor.putString(KEY_PHONE, telepon);
        editor.putString(KEY_RUMAHPOMPAID, rumah_pompa);
        editor.putString(KEY_PASSWORD, password);

        // commit changes
        editor.commit();
    }

    public void editSession(String username, String nama, String role, String alamat, String telepon, String rumah_pompa, String password) {

        editor.putBoolean(KEY_IS_LOGGEDIN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_NAME, nama);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_ADDRESS, alamat);
        editor.putString(KEY_PHONE, telepon);
        editor.putString(KEY_RUMAHPOMPAID, rumah_pompa);
        editor.putString(KEY_PASSWORD, password);

        // commit changes
        editor.commit();
    }

    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    public HashMap<String, String> getUser(){
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));
        user.put(KEY_ROLE, pref.getString(KEY_ROLE, null));
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_ADDRESS, pref.getString(KEY_ADDRESS, null));
        user.put(KEY_PHONE, pref.getString(KEY_PHONE, null));
        user.put(KEY_RUMAHPOMPAID, pref.getString(KEY_RUMAHPOMPAID, null));
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));

        return user;
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }
}
