package com.example.mardiana.alertsystemrumahpompa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.*;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddRumahPompaActivity extends AppCompatActivity {
    private TextView toolbar_title;
    private Button btn_add;
    private SessionManager session;
    private String role, apikey;
    private EditText edt_name, edt_address, edt_phone, edt_threshold, edt_latitude, edt_longitude, edt_kedalamansaluran;
    private Context mContext;
    private Volley mVolleyService;
    private int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rumah_pompa);

        mContext = this;
        mVolleyService = new Volley(this);

        SharedPreferences token = getSharedPreferences(AppConfig.PREF_APIKEY, MODE_PRIVATE);
        apikey = token.getString("apikey", "");

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tambah Rumah Pompa");

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = session.getUser();

        final String username = user.get(SessionManager.KEY_USERNAME);
        role = user.get(SessionManager.KEY_ROLE);

        btn_add = ((Button) findViewById(R.id.btn_rumahpompa_edit));
        btn_add.setText("Tambah");

        edt_name = ((EditText) findViewById(R.id.edt_rumahpompa_nama));
        edt_address = ((EditText) findViewById(R.id.edt_rumahpompa_alamat));
        edt_phone = ((EditText) findViewById(R.id.edt_rumahpompa_nohp));
        edt_threshold = ((EditText) findViewById(R.id.edt_rumahpompa_threshold));
        edt_latitude = ((EditText) findViewById(R.id.edt_rumahpompa_latitude));
        edt_longitude = ((EditText) findViewById(R.id.edt_rumahpompa_longitude));
        edt_kedalamansaluran = ((EditText) findViewById(R.id.edt_rumahpompa_kedalamansaluran));

        Button btn_map = (Button) findViewById(R.id.btn_launchmap);
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    //menjalankan place picker
                    startActivityForResult(builder.build(AddRumahPompaActivity.this), PLACE_PICKER_REQUEST);

                    // check apabila <a title="Solusi Tidak Bisa Download Google Play Services di Android" href="http://www.twoh.co/2014/11/solusi-tidak-bisa-download-google-play-services-di-android/" target="_blank">Google Play Services tidak terinstall</a> di HP
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                    android.util.Log.e("Place Picker", e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                    android.util.Log.e("Place Picker", e.getMessage());
                }
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nama = edt_name.getText().toString();
                String alamat = edt_address.getText().toString();
                String nohp = edt_phone.getText().toString();
                String threshold = edt_threshold.getText().toString();
                String latitude = edt_latitude.getText().toString();
                String longitude = edt_longitude.getText().toString();
                String kedalamansaluran = edt_kedalamansaluran.getText().toString();
                validateForm(nama, nohp, alamat, threshold, latitude, longitude, kedalamansaluran);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                /*String toastMsg = String.format(
                        "Place: %s \n" +
                                "Alamat: %s \n" +
                                "Latlng %s \n", place.getName(), place.getAddress(), place.getLatLng().latitude+" "+place.getLatLng().longitude);*/
                edt_latitude.setText(String.valueOf(place.getLatLng().latitude) );
                edt_longitude.setText(String.valueOf(place.getLatLng().longitude) );
            }
        }
    }

    private void store(final String name, final String phone, final String address, final String threshold,
                       final String depth, final String latitude, final String longitude){
        mVolleyService.addRumahPompa(name, phone, address, threshold, depth, latitude, longitude, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public ArrayList<String> onResponse(JSONObject response) {
                try {
                    boolean status = response.getBoolean("status");

                    if (status) {
                        Toast.makeText(getApplicationContext(), AppConfig.STORE_SUCCESS, Toast.LENGTH_LONG).show();

                        /*if (role.equals(AppConfig.PETUGAS)){
                            Intent intent_profil = new Intent(getBaseContext(), PetugasHomeActivity.class);
                            intent_profil.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent_profil);
                        }
                        else if (role.equals(AppConfig.ADMIN)){
                            //Intent intent_profil = new Intent(getBaseContext(), AdminHomeActivity.class);

                            *//*intent_profil.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent_profil);*//*
                        }*/
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                    else{
                        String kode = response.getString("kode");
                        if (kode.equals("1")){
                            Toast.makeText(getApplicationContext(), AppConfig.STORE_FAILED, Toast.LENGTH_LONG).show();
                        }else if (kode.equals("2")){
                            Toast.makeText(mContext, getString(R.string.invalid_token), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    private void validateForm(String nama, String nohp, String alamat, String threshold, String latitude, String longitude, String kedalamansaluran){
        edt_name.setError(null);
        edt_address.setError(null);
        edt_phone.setError(null);
        edt_threshold.setError(null);
        edt_latitude.setError(null);
        edt_longitude.setError(null);
        edt_kedalamansaluran.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username.
        if (TextUtils.isEmpty(nama)) {
            edt_name.setError(getString(R.string.error_field_required));
            focusView = edt_name;
            cancel = true;
        }

        if (TextUtils.isEmpty(alamat)) {
            edt_address.setError(getString(R.string.error_field_required));
            focusView = edt_address;
            cancel = true;
        }

        if (TextUtils.isEmpty(nohp)) {
            edt_phone.setError(getString(R.string.error_field_required));
            focusView = edt_phone;
            cancel = true;
        }

        if (TextUtils.isEmpty(threshold)) {
            edt_threshold.setError(getString(R.string.error_field_required));
            focusView = edt_threshold;
            cancel = true;
        }

        if (TextUtils.isEmpty(latitude)) {
            edt_latitude.setError(getString(R.string.error_field_required));
            focusView = edt_latitude;
            cancel = true;
        }

        if (TextUtils.isEmpty(longitude)) {
            edt_longitude.setError(getString(R.string.error_field_required));
            focusView = edt_longitude;
            cancel = true;
        }

        if (TextUtils.isEmpty(kedalamansaluran)) {
            edt_kedalamansaluran.setError(getString(R.string.error_field_required));
            focusView = edt_kedalamansaluran;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            store(nama, nohp, alamat, threshold, kedalamansaluran, latitude, longitude);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
