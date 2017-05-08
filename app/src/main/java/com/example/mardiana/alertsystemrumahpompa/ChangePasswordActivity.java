package com.example.mardiana.alertsystemrumahpompa;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText edt_oldpassword, edt_newpassword, edt_repassword;
    String username, apikey;
    Volley mVolleyService;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mContext = this;

        mVolleyService = new Volley(this);

        SharedPreferences token = getSharedPreferences(AppConfig.PREF_APIKEY, MODE_PRIVATE);
        apikey = token.getString("apikey", "");

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ubah Password");

        edt_oldpassword = ((EditText) findViewById(R.id.edt_oldpassword));
        edt_newpassword = ((EditText) findViewById(R.id.edt_newpassword));
        edt_repassword = ((EditText) findViewById(R.id.edt_repassword));
        Button btn_submit = ((Button) findViewById(R.id.btn_changepassword));

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attempSubmit(username);
            }
        });

    }

    private void attempSubmit(String username){
        edt_oldpassword.setError(null);
        edt_newpassword.setError(null);
        edt_repassword.setError(null);

        // Store values at the time of the login attempt.
        String oldpassword = edt_oldpassword.getText().toString();
        String newpassword = edt_newpassword.getText().toString();
        String repassword = edt_repassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username.
        if (TextUtils.isEmpty(oldpassword)) {
            edt_oldpassword.setError(getString(R.string.error_field_required));
            focusView = edt_oldpassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(newpassword)) {
            edt_newpassword.setError(getString(R.string.error_field_required));
            focusView = edt_newpassword;
            cancel = true;
        }else if ( edt_newpassword.getText().length()<5){
            edt_newpassword.setError(getString(R.string.lenght_incorrect_password));
            focusView = edt_newpassword;
            cancel = true;
        } else if (!isPasswordValid(newpassword)) {
            edt_newpassword.setError(getString(R.string.error_incorrect_password));
            focusView = edt_newpassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(repassword)) {
            edt_repassword.setError(getString(R.string.error_field_required));
            focusView = edt_repassword;
            cancel = true;
        }
        else if (!repassword.equals(newpassword)){
            edt_repassword.setError(getString(R.string.error_incorrect_repassword));
            focusView = edt_repassword;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            changePassword(username, oldpassword, newpassword);
        }
    }

    private boolean isPasswordValid(String password) {
        String password_pattern = "((?=.*\\d)(?=.*[a-zA-Z]).{5,})";

        Pattern pattern = Pattern.compile(password_pattern);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    private void changePassword(final String username, final String oldpassword, final String newpassword){
        mVolleyService.changePassword(username, oldpassword, newpassword, apikey, new VolleyResponseListener() {
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
                        Toast.makeText(ChangePasswordActivity.this, AppConfig.EDIT_SUCCESS, Toast.LENGTH_SHORT).show();
                        setResult(Activity.RESULT_OK);
                        finish();

                        //session.editSession(username, nama, role, alamat, nohp, idrumahpompa, password);
                    } else {
                        String kode = response.getString("kode");
                        if (kode.equals("2")){
                            showErrorDialog("Password Lama Salah");
                        }
                        else if (kode.equals("1")){
                            Toast.makeText(ChangePasswordActivity.this, AppConfig.EDIT_FAILED, Toast.LENGTH_SHORT).show();
                        }
                        else if (kode.equals("3")){
                            showErrorDialog("Masukkan Password Baru yang Berbeda dengan Password Lama");
                        }
                        else if (kode.equals("4")){
                            Toast.makeText(ChangePasswordActivity.this, getString(R.string.invalid_token), Toast.LENGTH_SHORT).show();
                        }
                        //Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*private void changePassword(final String username, final String oldpassword, final String newpassword) {
        String tag_string_req = "req_changepassword";
        //showProgress(true);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CHANGEPASSWORD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //showProgress(false);
                //Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                    if (status) {
                        String msg = jObj.getString("msg");
                        if (oldpassword.equals(newpassword)){
                            showErrorDialog("Masukkan Password Baru yang Berbeda dengan Password Lama");
                        }
                        else {
                            Toast.makeText(ChangePasswordActivity.this, AppConfig.EDIT_SUCCESS, Toast.LENGTH_SHORT).show();
                        }
                        //session.editSession(username, nama, role, alamat, nohp, idrumahpompa, password);
                    } else {
                        String kode = jObj.getString("kode");
                        if (kode.equals("2")){
                            showErrorDialog("Password Lama Salah");
                        }
                        else {
                            Toast.makeText(ChangePasswordActivity.this, AppConfig.EDIT_FAILED, Toast.LENGTH_SHORT).show();
                        }
                        //Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
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
            }
        }) {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showErrorDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
        builder.setTitle("Error");
        builder.setCancelable(true);
        builder.setMessage(msg);
        builder.setNegativeButton("OK", null);
        builder.show();
    }
}
