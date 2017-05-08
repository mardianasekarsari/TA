package com.example.mardiana.alertsystemrumahpompa;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddUserActivity extends AppCompatActivity {

    private Button btn_register;
    private View mProgressView, mRegisterFormView;
    private EditText edt_regusername, edt_regname, edt_regphone, edt_regaddress, edt_regpassword, edt_regrepassword;
    MaterialBetterSpinner role_spinner, rumahpompa_spinner;
    //Spinner role_spinner, rumahpompa_spinner;
    private static String[] role_list = new String[]{};
    private static String[] temp = new String[]{};
    private static String[] rumah_pompa = new String[]{};
    private static String[] role = new String[]{};
    private static String[] id = new String[]{};
    String rumahPompa;
    String username, apikey;
    String nama, nohp, alamat;
    String selected_role = "";
    String selected_rumahpompa = "";
    Boolean isAddUser;
    Volley mVolleyService;
    Context mContext;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        mContext = this;
        mVolleyService = new Volley(this);

        SharedPreferences token = getSharedPreferences(AppConfig.PREF_APIKEY, 0);
        apikey = token.getString("apikey", "");
        Toast.makeText(mContext, apikey, Toast.LENGTH_SHORT).show();

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);

        edt_regusername = ((EditText) findViewById(R.id.edt_reg_username));
        edt_regname = ((EditText) findViewById(R.id.edt_reg_nama));
        edt_regaddress = ((EditText) findViewById(R.id.edt_reg_alamat));
        edt_regphone = ((EditText) findViewById(R.id.edt_reg_nohp));
        edt_regpassword = ((EditText) findViewById(R.id.edt_reg_password));
        edt_regrepassword = ((EditText) findViewById(R.id.edt_reg_repassword));

        TextInputLayout til_regpassword = ((TextInputLayout) findViewById(R.id.til_reg_password));
        TextInputLayout til_regrepassword = ((TextInputLayout) findViewById(R.id.til_reg_repassword));

        Intent intent = getIntent();

        if (intent.hasExtra("username")){
            getSupportActionBar().setTitle("Ubah User");
            isAddUser = false;
            username = intent.getStringExtra("username");

            til_regpassword.setVisibility(View.GONE);
            til_regrepassword.setVisibility(View.GONE);
            edt_regusername.setEnabled(false);

            //set edit text
            mVolleyService.getBy(AppConfig.URL_USER + username, apikey, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        boolean status = response.getBoolean("status");

                        if (status) {
                            JSONObject result = response.getJSONObject("result");
                            nama = result.getString("nama_user");
                            nohp = result.getString("no_telp_user");
                            alamat = result.getString("alamat_user");

                            getRoleUser(username, new ServerCallback() {
                                @Override
                                public void onSuccess(String role) {
                                    // do stuff here
                                    edt_regusername.setText(username);
                                    //edt.setText(rumahpompa);
                                    edt_regname.setText(nama);
                                    edt_regaddress.setText(alamat);
                                    edt_regphone.setText(nohp);
                                    //role_spinner.setText(role);
                                    selected_role = role;
                                    getAllRole();
                                }
                            });
                        }
                        else {
                            Toast.makeText(mContext, getString(R.string.invalid_token), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            getUserRumahPompa(username, apikey);
        }
        else {
            getSupportActionBar().setTitle("Tambah User");
            isAddUser = true;
            getAllRole();
            getAllRumahPompa();
        }

        btn_register = ((Button) findViewById(R.id.btn_register));
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        //rumahpompa_spinner.getText().toString();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void getAllRumahPompa(){
        mVolleyService.getAll(AppConfig.URL_RUMAHPOMPA, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Boolean status = response.getBoolean("status");
                    if (status){
                        JSONArray result = response.getJSONArray("result");

                        rumah_pompa = new String[result.length()];
                        id = new String[result.length()];
                        for (int i=0; i<result.length(); i++){
                            JSONObject re = result.getJSONObject(i);
                            rumah_pompa[i] = re.getString("nama_");
                            id[i] = re.getString("id_rumah_pompa");
                        }

                        ArrayAdapter<String> arrayAdapter_rumahPompa = new ArrayAdapter<String>(AddUserActivity.this, android.R.layout.simple_dropdown_item_1line, rumah_pompa);
                        rumahpompa_spinner = ((MaterialBetterSpinner) findViewById(R.id.spin_rumah_pompa));
                        rumahpompa_spinner.setAdapter(arrayAdapter_rumahPompa);
                        if (!selected_rumahpompa.equals("")){
                            rumahpompa_spinner.setText(selected_rumahpompa);
                        }
                    }else {
                        Toast.makeText(mContext, getString(R.string.invalid_token), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /*private void getAllRumahPompa(){
        String tag_string_req = "req_getallrumahpompa";
        showProgress(true);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GETRUMAHPOMPA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                showProgress(false);
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray result = jObj.getJSONArray("result");

                    rumah_pompa = new String[result.length()];
                    id = new String[result.length()];
                    for (int i=0; i<result.length(); i++){
                        JSONObject re = result.getJSONObject(i);
                        rumah_pompa[i] = re.getString("nama_");
                        id[i] = re.getString("id_rumah_pompa");
                    }

                    ArrayAdapter<String> arrayAdapter_rumahPompa = new ArrayAdapter<String>(AddUserActivity.this, android.R.layout.simple_dropdown_item_1line, rumah_pompa);
                    rumahpompa_spinner = ((MaterialBetterSpinner) findViewById(R.id.spin_rumah_pompa));
                    rumahpompa_spinner.setAdapter(arrayAdapter_rumahPompa);
                    if (!selected_rumahpompa.equals("")){
                        rumahpompa_spinner.setText(selected_rumahpompa);
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
                Log.e("Error", "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }*/

    private void getAllRole(){
        mVolleyService.getAll(AppConfig.URL_ROLE, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean status = response.getBoolean("status");
                    if (status){
                        JSONArray result = response.getJSONArray("result");

                        temp = new String[result.length()];
                        role_list = new String[result.length()];
                        role = new String[result.length()];
                        //id = new String[result.length()];
                        for (int i=0; i<result.length(); i++){
                            JSONObject re = result.getJSONObject(i);
                            role[i] = re.getString("nama_role");
                            temp[i] = role[i].toLowerCase();
                            role_list[i] = temp[i].substring(0, 1).toUpperCase() + temp[i].substring(1);
                            //id[i] = re.getString("id_role");
                        }

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddUserActivity.this, android.R.layout.simple_dropdown_item_1line, role_list);
                        role_spinner = ((MaterialBetterSpinner) findViewById(R.id.spin_role_spinner));
                        role_spinner.setAdapter(arrayAdapter);
                        if (!selected_role.equals("")){
                            role_spinner.setText(selected_role);
                        }
                    }
                    else {
                        Toast.makeText(mContext, getString(R.string.invalid_token), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*private void getAllRole(){
        String tag_string_req = "req_getallrole";
        //showProgress(true);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GETROLE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //showProgress(false);
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray result = jObj.getJSONArray("result");

                    temp = new String[result.length()];
                    role_list = new String[result.length()];
                    role = new String[result.length()];
                    //id = new String[result.length()];
                    for (int i=0; i<result.length(); i++){
                        JSONObject re = result.getJSONObject(i);
                        role[i] = re.getString("nama_role");
                        temp[i] = role[i].toLowerCase();
                        role_list[i] = temp[i].substring(0, 1).toUpperCase() + temp[i].substring(1);
                        //id[i] = re.getString("id_role");
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddUserActivity.this, android.R.layout.simple_dropdown_item_1line, role_list);
                    role_spinner = ((MaterialBetterSpinner) findViewById(R.id.spin_role_spinner));
                    role_spinner.setAdapter(arrayAdapter);
                    if (!selected_role.equals("")){
                        role_spinner.setText(selected_role);
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
                Log.e("Error", "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //showProgress(false);
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }*/

    private void getUserRumahPompa(final String username, final String apikey){
        mVolleyService.getUserRumahPompa(username, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean status = response.getBoolean("status");
                    if (status){
                        JSONObject result = response.getJSONObject("result");
                        selected_rumahpompa = result.getString("nama_");
                        getAllRumahPompa();
                    }
                    else {
                        Toast.makeText(mContext, getString(R.string.invalid_token), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*private void getUserRumahPompa(final String username, final String apikey){
        String tag_string_req = "req_getuserrumahpompa";
        //showProgress(true);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETUSERRUMAHPOMPA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Toast.makeText(getActivity().getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                //showProgress(false);
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject result = jObj.getJSONObject("result");
                    selected_rumahpompa = result.getString("nama_");
                    getAllRumahPompa();
                    //Toast.makeText(getActivity().getApplicationContext(), rumah_pompa[0], Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //Toast.makeText(getActivity().getApplicationContext(), "Json2 error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //showProgress(false);
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);

                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }*/


    private void attemptRegister() {
        edt_regusername.setError(null);
        edt_regname.setError(null);
        edt_regaddress.setError(null);
        edt_regphone.setError(null);
        edt_regpassword.setError(null);
        edt_regrepassword.setError(null);
        role_spinner.setError(null);
        rumahpompa_spinner.setError(null);

        // Store values at the time of the login attempt.
        String username = edt_regusername.getText().toString();
        String name = edt_regname.getText().toString();
        String address = edt_regaddress.getText().toString();
        String phone = edt_regphone.getText().toString();
        String role = role_spinner.getText().toString();
        String password = edt_regpassword.getText().toString();
        String repassword = edt_regrepassword.getText().toString();
        rumahPompa = rumahpompa_spinner.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        /*if (!isPasswordValid(password) && !isPasswordValid(password)) {

            edt_password.setError(getString(R.string.error_invalid_password));
            focusView = edt_password;
            cancel = true;
        }*/

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            edt_regusername.setError(getString(R.string.error_field_required));
            focusView = edt_regusername;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            edt_regusername.setError(getString(R.string.error_invalid_username));
            focusView = edt_regusername;
            cancel = true;
        }

        if (TextUtils.isEmpty(name)) {
            edt_regname.setError(getString(R.string.error_field_required));
            focusView = edt_regname;
            cancel = true;
        } else if (!isNameValid(name)) {
            edt_regname.setError(getString(R.string.error_invalid_name));
            focusView = edt_regname;
            cancel = true;
        }

        if (TextUtils.isEmpty(address)) {
            edt_regaddress.setError(getString(R.string.error_field_required));
            focusView = edt_regaddress;
            cancel = true;
        }

        if (TextUtils.isEmpty(phone)) {
            edt_regphone.setError(getString(R.string.error_field_required));
            focusView = edt_regphone;
            cancel = true;
        } /*else if (!isPhoneValid(phone)) {
            edt_regphone.setError(getString(R.string.error_invalid_phone));
            focusView = edt_regphone;
            cancel = true;
        }*/

        if (isAddUser){
            if (TextUtils.isEmpty(password)) {
                edt_regpassword.setError(getString(R.string.error_field_required));
                focusView = edt_regpassword;
                cancel = true;
            } else if ( edt_regpassword.getText().length()<5){
                edt_regpassword.setError(getString(R.string.lenght_incorrect_password));
                focusView = edt_regpassword;
                cancel = true;
            } else if (!isPasswordValid(password)) {
                edt_regpassword.setError(getString(R.string.error_incorrect_password));
                focusView = edt_regpassword;
                cancel = true;
            }

            if (TextUtils.isEmpty(repassword)) {
                edt_regrepassword.setError(getString(R.string.error_field_required));
                focusView = edt_regpassword;
                cancel = true;
            } else if (!isRepasswordValid(password, repassword)) {
                edt_regrepassword.setError(getString(R.string.error_incorrect_repassword));
                focusView = edt_regrepassword;
                cancel = true;
            }

            if (role_spinner.getText().toString().equals("")){
                role_spinner.setError(getString(R.string.error_field_required));
                focusView = role_spinner;
                cancel = true;
            }

            if (rumahpompa_spinner.getText().toString().equals("")){
                rumahpompa_spinner.setError(getString(R.string.error_field_required));
                focusView = rumahpompa_spinner;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            if (isAddUser){
                register(username, rumahPompa, name, role, address, phone, password);
            }
            else {
                edit(username, rumahPompa, name, role, address, phone);
            }
        }
    }

    private void register(final String username, final String rumah_pompa, final String name, final String role, final String address, final String phone, final String password){
        mVolleyService.register(username, rumah_pompa, name, role, address, phone, password, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean status = response.getBoolean("status");

                    if (status) {
                        String msg = response.getString("msg");
                        Toast.makeText(AddUserActivity.this, AppConfig.STORE_SUCCESS, Toast.LENGTH_SHORT).show();
                        setResult(Activity.RESULT_OK);
                        finish();

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = response.getString("msg");
                        String kode = response.getString("kode");
                        if (kode.equals("1")){
                            Toast.makeText(AddUserActivity.this, AppConfig.STORE_FAILED, Toast.LENGTH_SHORT).show();
                        }else if (kode.equals("2")){
                            Toast.makeText(AddUserActivity.this, "Username Sudah Terpakai", Toast.LENGTH_SHORT).show();
                        }else if (kode.equals("3")){
                            Toast.makeText(AddUserActivity.this, getString(R.string.invalid_token), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void edit(final String username, final String rumah_pompa, final String name, final String role, final String address, final String phone){
        mVolleyService.editUser(username, rumah_pompa, name, role, address, phone, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean status = response.getBoolean("status");

                    if (status) {
                        String msg = response.getString("msg");
                        Toast.makeText(AddUserActivity.this, AppConfig.EDIT_SUCCESS, Toast.LENGTH_SHORT).show();
                        setResult(Activity.RESULT_OK);
                        finish();

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = response.getString("msg");
                        String kode = response.getString("kode");
                        if (kode.equals("1")){
                            Toast.makeText(AddUserActivity.this, AppConfig.EDIT_FAILED, Toast.LENGTH_SHORT).show();
                        }else if (kode.equals("2")){
                            Toast.makeText(AddUserActivity.this, getString(R.string.invalid_token), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*private void register(final String username, final String rumah_pompa, final String name, final String role, final String address, final String phone, final String password) {
        String tag_string_req = "req_register";
        //showProgress(true);

        //Toast.makeText(this, "username: " + username + " rumah_pompa: " + rumah_pompa + " name: " + name + " role: " + role + " address: " + address + " phone: " + phone + " password: " + password, Toast.LENGTH_SHORT).show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //showProgress(false);
                //Toast.makeText(AddUserActivity.this, "username: " + username + " rumah_pompa: " + rumah_pompa + " name: " + name + " role: " + role + " address: " + address + " phone: " + phone + " password: " + password, Toast.LENGTH_SHORT).show();
                //Toast.makeText(AddUserActivity.this, response, Toast.LENGTH_SHORT).show();
                //Toast.makeText(AddUserActivity.this, "role: ", Toast.LENGTH_SHORT).show();
                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                    if (status) {
                        String msg = jObj.getString("msg");
                        Toast.makeText(AddUserActivity.this, AppConfig.STORE_SUCCESS, Toast.LENGTH_SHORT).show();
                        *//*Intent intent_profil = new Intent(getBaseContext(), AdminHomeActivity.class);
                        intent_profil.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent_profil);*//*

                        finish();

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("msg");
                        Toast.makeText(AddUserActivity.this, AppConfig.STORE_FAILED, Toast.LENGTH_SHORT).show();

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
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("name", name);
                params.put("address", address);
                params.put("phone", phone);
                params.put("password", password);
                params.put("role", role);
                params.put("rumah_pompa", rumah_pompa);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }*/

    /*private void edit(final String username, final String rumah_pompa, final String name, final String role, final String address, final String phone) {
        String tag_string_req = "req_edituser";
        //showProgress(true);

        //Toast.makeText(this, "username: " + username + " rumah_pompa: " + rumah_pompa + " name: " + name + " role: " + role + " address: " + address + " phone: " + phone + " password: " + password, Toast.LENGTH_SHORT).show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_EDITUSER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //showProgress(false);
                //Toast.makeText(AddUserActivity.this, "username: " + username + " rumah_pompa: " + rumah_pompa + " name: " + name + " role: " + role + " address: " + address + " phone: " + phone + " password: " + password, Toast.LENGTH_SHORT).show();
                //Toast.makeText(AddUserActivity.this, response, Toast.LENGTH_SHORT).show();
                //Toast.makeText(AddUserActivity.this, "role: ", Toast.LENGTH_SHORT).show();
                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                    if (status) {
                        String msg = jObj.getString("msg");
                        Toast.makeText(AddUserActivity.this, AppConfig.EDIT_SUCCESS, Toast.LENGTH_SHORT).show();
                        *//*Intent intent_profil = new Intent(getBaseContext(), AdminHomeActivity.class);
                        intent_profil.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent_profil);*//*

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
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("name", name);
                params.put("address", address);
                params.put("phone", phone);
                params.put("role", role);
                params.put("rumah_pompa", rumah_pompa);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }*/

    private boolean isUsernameValid(String username) {
        String username_pattern = "^[a-zA-Z0-9._-]{3,50}$";

        Pattern pattern = Pattern.compile(username_pattern);
        Matcher matcher = pattern.matcher(username);

        return matcher.matches();
    }

    private boolean isNameValid(String name) {
        String name_pattern = "^[\\p{L} .'-]+$";

        Pattern pattern = Pattern.compile(name_pattern);
        Matcher matcher = pattern.matcher(name);

        return matcher.matches();
    }

    private boolean isPhoneValid(String phone) {
        String phone_pattern = "^(1\\-)?[0-9]{3}\\-?[0-9]{3}\\-?[0-9]{4}$";

        Pattern pattern = Pattern.compile(phone_pattern);
        Matcher matcher = pattern.matcher(phone);

        return matcher.matches();
    }

    /*private boolean isPasswordValid(String password) {
        String password_pattern = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{5,20})";

        Pattern pattern = Pattern.compile(password_pattern);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }*/

    private boolean isPasswordValid(String password) {
        String password_pattern = "((?=.*\\d)(?=.*[a-zA-Z]).{5,})";

        Pattern pattern = Pattern.compile(password_pattern);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    private boolean isRepasswordValid(String password, String repassword){
        if(repassword.equals(password))
            return true;
        else
            return false;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Register Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private void getRoleUser(final String username, final ServerCallback callback){
        mVolleyService.getBy(AppConfig.URL_ROLE + username, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean status = response.getBoolean("status");

                    if (status) {
                        JSONObject result = response.getJSONObject("result");
                        String role = result.getString("nama_role");
                        callback.onSuccess(role);
                    }
                    else {
                        Toast.makeText(AddUserActivity.this, getString(R.string.invalid_token), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*private void getRoleUser(final String username, final ServerCallback callback){
        String tag_string_req = "req_getrolebyusername";
        //showProgress(true);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETROLEBYUSERNAME, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Toast.makeText(getActivity().getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                //showProgress(false);
                try {
                    JSONObject jObj = new JSONObject(response);
                    //JSONObject result = jObj.getJSONObject("result");
                    String role = jObj.getString("nama_role");
                    callback.onSuccess(role);

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //Toast.makeText(getActivity().getApplicationContext(), "Json2 error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //showProgress(false);
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);

                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }*/

    public interface ServerCallback {
        void onSuccess(String role);
    }

}
