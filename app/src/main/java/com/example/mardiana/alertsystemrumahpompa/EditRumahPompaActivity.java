package com.example.mardiana.alertsystemrumahpompa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

    private void edit(final String id, final String name, final String phone, final String address, final String threshold, final String latitude, final String longitude, final String kedalamansaluran) {
        mVolleyService.editRumahPompa(id, name, phone, address, threshold, latitude, longitude, kedalamansaluran, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(JSONObject response) {
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*private void edit(final String id, final String name, final String phone, final String address, final String threshold, final String latitude, final String longitude, final String kedalamansaluran) {
        String tag_string_req = "req_edit_rumahpompa";
        //showProgress(true);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_EDITRUMAHPOMPA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //showProgress(false);
                //Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

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
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //showProgress(false);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("name", name);
                params.put("phone", phone);
                params.put("address", address);
                params.put("threshold", threshold);
                params.put("latitude", latitude);
                params.put("longitude", longitude);
                params.put("depthofinlet", kedalamansaluran);

                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }*/

    private void getrumahpompabyId(final String id) {
        String url = AppConfig.URL_RUMAHPOMPA + id;
        mVolleyService.getBy(url, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    nama_rumahpompa = response.getString("nama_");
                    alamat_rumahpompa = response.getString("jalan");
                    nohp_rumahpompa = response.getString("no_telp_rumah_pompa");
                    threshold_rumahpompa = response.getString("threshold_tinggi_air");
                    final String latitude_rumahpompa = response.getString("latitude");
                    String longitude_rumahpompa = response.getString("longitude");
                    String depthofriver_rumahpompa = response.getString("ketinggian_sungai");

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

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*private void getrumahpompabyId(final String id) {
        String tag_string_req = "req_rumahpompa";
        //showProgress(true);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETRUMAHPOMPABYID, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //showProgress(false);
                //Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();

                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject result = jObj.getJSONObject("result");

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



                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //showProgress(false);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);

                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }*/

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
