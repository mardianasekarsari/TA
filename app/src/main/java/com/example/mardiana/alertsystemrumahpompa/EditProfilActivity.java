package com.example.mardiana.alertsystemrumahpompa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfilActivity extends AppCompatActivity {

    private EditText edt_username, edt_name, edt_phone, edt_address, edt_password, edt_rumahpompa;
    private MaterialBetterSpinner spinner_rumahpompa;
    private static String[] rumah_pompa = new String[]{};
    private static String[] id = new String[]{};
    private static String rumahpompa="";
    String idrumahpompa,role, password, apikey;
    private Button btn_edit;
    SessionManager session;
    LoginSQLiteHandler db;
    Context mContext;
    Volley mVolleyService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ubah Profil");

        setContentView(R.layout.activity_edit_profil);

        mContext = this;
        mVolleyService = new Volley(this);

        SharedPreferences token = getSharedPreferences(AppConfig.PREF_APIKEY, MODE_PRIVATE);
        apikey = token.getString("apikey", "");

        db = new LoginSQLiteHandler(this);
        session = new SessionManager(getApplicationContext());

        edt_username = ((EditText) findViewById(R.id.edt_profil_username));
        edt_username.setEnabled(false);

        edt_name = ((EditText) findViewById(R.id.edt_profil_nama));
        edt_address = ((EditText) findViewById(R.id.edt_profil_alamat));
        edt_phone = ((EditText) findViewById(R.id.edt_profil_nohp));
        /*spinner_rumahpompa = ((MaterialBetterSpinner) findViewById(R.id.spin_profil_rumahpompa));*/
        edt_rumahpompa = ((EditText) findViewById(R.id.edt_profil_rumahpompa));
        edt_rumahpompa.setEnabled(false);

        //getAllRumahPompa();

        /*HashMap<String, String> user = db.getUserDetails();

        String username = user.get("username");
        String nama = user.get("nama");
        String nohp = user.get("telepon_user");
        String alamat = user.get("alamat_user");
        String idrumahpompa = user.get("rumahpompa");*/

        HashMap<String, String> user = session.getUser();

        String username = user.get(SessionManager.KEY_USERNAME);
        String nama = user.get(SessionManager.KEY_NAME);
        String nohp = user.get(SessionManager.KEY_PHONE);
        String alamat = user.get(SessionManager.KEY_ADDRESS);
        role = user.get(SessionManager.KEY_ROLE);
        idrumahpompa = user.get(SessionManager.KEY_RUMAHPOMPAID);
        password = user.get(SessionManager.KEY_PASSWORD);

        getrumahpompabyId(idrumahpompa);

        edt_username.setText(username);
        edt_name.setText(nama);
        edt_phone.setText(nohp);
        edt_address.setText(alamat);

        btn_edit = ((Button) findViewById(R.id.btn_profil_edit));
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateForm();
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
            public ArrayList<String> onResponse(JSONObject response) {
                try {
                    boolean status = response.getBoolean("status");

                    if (status) {
                        db.editUser(username, name, phone, address);
                        session.editSession(username, name, role, address, phone, idrumahpompa, password);
                        Toast.makeText(getApplicationContext(), AppConfig.EDIT_SUCCESS, Toast.LENGTH_LONG).show();

                        if (role.equals("PETUGAS")){
                            Intent intent_profil = new Intent(getBaseContext(), PetugasHomeActivity.class);
                            intent_profil.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent_profil);
                        }
                        else if (role.equals("PENGAWAS")){
                            Intent intent_profil = new Intent(getBaseContext(), PengawasHomeActivity.class);
                            intent_profil.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent_profil);
                        }else if (role.equals("ADMIN")){
                            Intent intent_profil = new Intent(getBaseContext(), AdminHomeActivity.class);
                            intent_profil.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent_profil);
                        }

                        finish();
                        Log.d("EditProfil", AppConfig.EDIT_SUCCESS);

                    } else {
                        String kode = response.getString("kode");
                        String errorMsg = response.getString("msg");
                        Log.d("EditProfil", errorMsg);
                        if (kode.equals("1")){
                            Toast.makeText(getApplicationContext(), AppConfig.EDIT_FAILED, Toast.LENGTH_LONG).show();
                        }else if (kode.equals("2")){
                            Toast.makeText(mContext, getString(R.string.invalid_token), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("EditProfil", "JsonException");
                }
                return null;
            }
        });
    }

    private void validateForm(){
        edt_name.setError(null);
        edt_address.setError(null);
        edt_phone.setError(null);


        boolean cancel = false;
        View focusView = null;

        String username = edt_username.getText().toString();
        String name = edt_name.getText().toString();
        String phone = edt_phone.getText().toString();
        String address = edt_address.getText().toString();
        String rumahpompa = edt_rumahpompa.getText().toString();


        // Check for a valid username.
        if (TextUtils.isEmpty(name)) {
            edt_name.setError(getString(R.string.error_field_required));
            focusView = edt_name;
            cancel = true;
        } else if (!isNameValid(name)) {
            edt_name.setError(getString(R.string.error_invalid_name));
            focusView = edt_name;
            cancel = true;
        }

        if (TextUtils.isEmpty(address)) {
            edt_address.setError(getString(R.string.error_field_required));
            focusView = edt_address;
            cancel = true;
        }

        if (TextUtils.isEmpty(phone)) {
            edt_phone.setError(getString(R.string.error_field_required));
            focusView = edt_phone;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            edit(username, rumahpompa, name, role, address, phone);
        }
    }

    private boolean isNameValid(String name) {
        String name_pattern = "^[\\p{L} .'-]+$";

        Pattern pattern = Pattern.compile(name_pattern);
        Matcher matcher = pattern.matcher(name);

        return matcher.matches();
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
                        rumahpompa = result.getString("nama_");
                        //spinner_rumahpompa.setHint(rumahpompa);
                        edt_rumahpompa.setText(rumahpompa);
                        //Toast.makeText(EditProfilActivity.this, rumahpompa, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String errorMsg = response.getString("msg");
                        Toast.makeText(mContext, getString(R.string.invalid_token), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
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
