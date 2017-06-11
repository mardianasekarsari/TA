package com.example.mardiana.alertsystemrumahpompa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EditRumahPompaActivity extends AppCompatActivity {

    private LoginSQLiteHandler db;
    private SessionManager session;
    private FloatingActionButton fab_edit;
    private TextInputLayout til_rumahpompa_nama, til_rumahpompa_alamat, til_rumahpompa_nohp, til_rumahpompa_threshold, til_rumahpompa_latitude, til_rumahpompa_longitude;
    private EditText edt_name, edt_address, edt_phone, edt_threshold, edt_latitude, edt_longitude, edt_depth;
    private Button btn_edit;
    private static String nama_rumahpompa="";
    private static String alamat_rumahpompa="";
    private static String nohp_rumahpompa="";
    private static String threshold_rumahpompa="";
    private String nama, alamat, nohp, threshold, latitude, longitude, depthofriver;
    private String idrumahpompa, role, id, apikey;
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

        db = new LoginSQLiteHandler(this);
        session = new SessionManager(this.getApplicationContext());

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Rumah Pompa");

        til_rumahpompa_nama = ((TextInputLayout) findViewById(R.id.til_rumahpompa_nama));
        til_rumahpompa_alamat = ((TextInputLayout) findViewById(R.id.til_rumahpompa_alamat));
        til_rumahpompa_nohp = ((TextInputLayout) findViewById(R.id.til_rumahpompa_nohp));
        til_rumahpompa_threshold = ((TextInputLayout) findViewById(R.id.til_rumahpompa_threshold));
        til_rumahpompa_latitude = ((TextInputLayout) findViewById(R.id.til_rumahpompa_latitude));
        til_rumahpompa_longitude = ((TextInputLayout) findViewById(R.id.til_rumahpompa_longitude));

        edt_name = ((EditText) findViewById(R.id.edt_rumahpompa_nama));
        edt_address = ((EditText) findViewById(R.id.edt_rumahpompa_alamat));
        edt_phone = ((EditText) findViewById(R.id.edt_rumahpompa_nohp));
        edt_threshold = ((EditText) findViewById(R.id.edt_rumahpompa_threshold));
        edt_latitude = ((EditText) findViewById(R.id.edt_rumahpompa_latitude));
        edt_longitude = ((EditText) findViewById(R.id.edt_rumahpompa_longitude));
        edt_depth = ((EditText) findViewById(R.id.edt_rumahpompa_kedalamansaluran));

        btn_edit = ((Button) findViewById(R.id.btn_rumahpompa_edit));

        Button btn_map = (Button) findViewById(R.id.btn_launchmap);
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    //menjalankan place picker
                    startActivityForResult(builder.build(EditRumahPompaActivity.this), PLACE_PICKER_REQUEST);

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

        //fab_edit = ((FloatingActionButton) findViewById(R.id.fab_rumahpompa_edit));

        HashMap<String, String> user = db.getUserDetails();

        String username = user.get("username");
        String nama = user.get("nama");
        String nohp = user.get("telepon_user");
        String alamat = user.get("alamat_user");
        idrumahpompa = user.get("rumahpompa");
        role = user.get("role_user");

        if(role.equals(AppConfig.PETUGAS)){
            //Toast.makeText(this, "Lala", Toast.LENGTH_SHORT).show();
            btn_map.setVisibility(View.GONE);
            til_rumahpompa_nama.setVisibility(View.GONE);
            til_rumahpompa_nohp.setVisibility(View.GONE);
            til_rumahpompa_alamat.setVisibility(View.GONE);
            til_rumahpompa_latitude.setVisibility(View.GONE);
            til_rumahpompa_longitude.setVisibility(View.GONE);
            getrumahpompabyId(idrumahpompa);
        }else if (role.equals(AppConfig.ADMIN)){
            Intent intent = getIntent();
            id = intent.getStringExtra("id");
            getrumahpompabyId(id);
        }
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

    private void edit(final String id, final String name, final String phone, final String address, final String threshold, final String latitude, final String longitude, final String kedalamansaluran) {
        mVolleyService.editRumahPompa(id, name, phone, address, threshold, latitude, longitude, kedalamansaluran, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public ArrayList<String> onResponse(JSONObject response) {
                try {
                    boolean status = response.getBoolean("status");

                    if (status) {
                        Toast.makeText(getApplicationContext(), AppConfig.EDIT_SUCCESS, Toast.LENGTH_LONG).show();

                        if (role.equals(AppConfig.PETUGAS)){
                            Intent intent_profil = new Intent(getBaseContext(), PetugasHomeActivity.class);
                            intent_profil.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent_profil);
                        }
                        else if (role.equals(AppConfig.ADMIN)){
                            Intent intent_profil = new Intent(getBaseContext(), RumahPompaActivity.class);
                            intent_profil.putExtra("id", id);
                            setResult(RESULT_OK, intent_profil);
                            intent_profil.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent_profil);
                        }

                        finish();
                    }
                    else {
                        String kode = response.getString("kode");
                        String errorMsg = response.getString("msg");
                        if (kode.equals("1")){
                            Toast.makeText(getApplicationContext(), AppConfig.EDIT_FAILED, Toast.LENGTH_LONG).show();
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

    private void getrumahpompabyId(final String id) {
        String url = AppConfig.URL_RUMAHPOMPA + id;
        mVolleyService.getBy(url, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public ArrayList<String> onResponse(JSONObject response) {
                try {
                    boolean status = response.getBoolean("status");

                    if (status) {
                        JSONObject result = response.getJSONObject("result");
                        nama_rumahpompa = result.getString("nama_");
                        alamat_rumahpompa = result.getString("jalan");
                        nohp_rumahpompa = result.getString("no_telp_rumah_pompa");
                        threshold_rumahpompa = result.getString("threshold_tinggi_air");
                        final String latitude_rumahpompa = result.getString("latitude");
                        String longitude_rumahpompa = result.getString("longitude");
                        String depthofriver_rumahpompa = result.getString("ketinggian_sungai");

                        edt_threshold.setText(threshold_rumahpompa);
                        edt_name.setText(nama_rumahpompa);
                        edt_address.setText(alamat_rumahpompa);
                        edt_phone.setText(nohp_rumahpompa);
                        edt_latitude.setText(latitude_rumahpompa);
                        edt_longitude.setText(longitude_rumahpompa);
                        edt_depth.setText(depthofriver_rumahpompa);

                        btn_edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                nama = edt_name.getText().toString();
                                alamat = edt_address.getText().toString();
                                nohp = edt_phone.getText().toString();
                                threshold = edt_threshold.getText().toString();
                                latitude = edt_latitude.getText().toString();
                                longitude = edt_longitude.getText().toString();
                                depthofriver = edt_depth.getText().toString();

                                //if(role.equals("PETUGAS")){
                                validateForm(nama, nohp, alamat, threshold, latitude, longitude, depthofriver);
                                //}
                            }
                        });
                    }else {
                        Toast.makeText(mContext, getString(R.string.invalid_token), Toast.LENGTH_SHORT).show();
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
        edt_depth.setError(null);

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
            edt_depth.setError(getString(R.string.error_field_required));
            focusView = edt_depth;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            if (role.equals(AppConfig.PETUGAS))
                edit(idrumahpompa, nama, nohp, alamat, threshold, latitude, longitude, kedalamansaluran);
            else if (role.equals(AppConfig.ADMIN)){
                edit(id, nama, nohp, alamat, threshold, latitude, longitude, kedalamansaluran);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
