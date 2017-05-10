package com.example.mardiana.alertsystemrumahpompa;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class UserDetailActivity extends AppCompatActivity {
    private TextView tv_username, tv_name, tv_phonenumber, tv_address, tv_rumahpompa, tv_role;
    String rumahpompa = "";
    String username = "";
    String nama, nohp, alamat, apikey;

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
        setContentView(R.layout.activity_user_detail);
        mContext = this;
        mVolleyService = new Volley(this);

        SharedPreferences token = getSharedPreferences(AppConfig.PREF_APIKEY, MODE_PRIVATE);
        apikey = token.getString("apikey", "");

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("User");

        Intent intent = getIntent();
        rumahpompa = intent.getStringExtra("rumahpompa");
        username = intent.getStringExtra("username");

        tv_username = ((TextView) findViewById(R.id.tv_user_username));
        tv_name = ((TextView) findViewById(R.id.tv_user_nama));
        tv_phonenumber = ((TextView) findViewById(R.id.tv_user_nohp));
        tv_address = ((TextView) findViewById(R.id.tv_user_alamat));
        tv_rumahpompa = ((TextView) findViewById(R.id.tv_user_rumahpompa));
        tv_role = ((TextView) findViewById(R.id.tv_user_role));

        //getUserByUsername(username);
        mVolleyService.getBy(AppConfig.URL_USER + username, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Boolean status = response.getBoolean("status");
                    if (status){
                        JSONObject result = response.getJSONObject("result");
                        nama = result.getString("nama_user");
                        nohp = result.getString("no_telp_user");
                        alamat = result.getString("alamat_user");
                        getRoleUser(username, new ServerCallback() {
                            @Override
                            public void onSuccess(String role) {
                                // do stuff here
                                tv_username.setText(username);
                                //tv_rumahpompa.setText(rumahpompa);
                                tv_name.setText(nama);
                                tv_address.setText(alamat);
                                tv_phonenumber.setText(nohp);
                                tv_role.setText(role);
                            }
                        });
                        getUserRumahPompa(username, new ServerCallback() {
                            @Override
                            public void onSuccess(String nama_rumahpompa) {
                                tv_rumahpompa.setText(nama_rumahpompa);
                            }
                        });
                    }
                    else {
                        Toast.makeText(mContext, getString(R.string.invalid_token), Toast.LENGTH_SHORT).show();
                    }
                    //getRoleUser(username);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getRoleUser(final String username, final ServerCallback callback){
        String url = AppConfig.URL_ROLE + username;
        mVolleyService.getBy(url, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Boolean status = response.getBoolean("status");
                    if (status){
                        JSONObject result = response.getJSONObject("result");
                        String role = result.getString("nama_role");
                        callback.onSuccess(role);
                    }else {
                        Toast.makeText(mContext, getString(R.string.invalid_token), Toast.LENGTH_SHORT).show();
                    }
                    //JSONObject jObj = new JSONObject(response);
                    //JSONObject result = jObj.getJSONObject("result");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getUserRumahPompa(final String username, final ServerCallback callback){
        String url = AppConfig.URL_USERRUMAHPOMPA + username;
        mVolleyService.getBy(url, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Boolean status = response.getBoolean("status");
                    if (status){
                        JSONObject result = response.getJSONObject("result");
                        String nama_rumahpompa = result.getString("nama_");
                        callback.onSuccess(nama_rumahpompa);
                    }else {
                        Toast.makeText(mContext, getString(R.string.invalid_token), Toast.LENGTH_SHORT).show();
                    }
                    //JSONObject jObj = new JSONObject(response);
                    //JSONObject result = jObj.getJSONObject("result");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

   /* private void getRoleUser(final String username, final ServerCallback callback) {
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
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_rumahpompa, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_rumahpompa, menu);
        return true;
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
            case R.id.action_edit:
                edit();
                return true;
            case R.id.action_delete:
                confirmDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void edit() {
        Intent intent = new Intent(this, AddUserActivity.class);
        intent.putExtra("username", username);
        //startA(intent);
        startActivityForResult(intent, 10002);
    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserDetailActivity.this);
        builder.setTitle("Hapus Data");
        builder.setCancelable(true);
        builder.setMessage("Apakah Anda Yakin Untuk Menghapus Data?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int ids) {
                delete(username);
            }
        });
        builder.setNegativeButton("NO", null);
        builder.show();
    }

    private void delete(String username){
        String url = AppConfig.URL_USER + username;
        mVolleyService.delete(url, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean status = response.getBoolean("status");

                    if (status) {
                        Toast.makeText(UserDetailActivity.this, AppConfig.DELETE_SUCCESS, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String kode = response.getString("kode");
                        if (kode.equals("1")){
                            Toast.makeText(UserDetailActivity.this, AppConfig.DELETE_FAILED, Toast.LENGTH_SHORT).show();
                        }else if (kode.equals("2")){
                            Toast.makeText(mContext, getString(R.string.invalid_token), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface ServerCallback {
        void onSuccess(String role);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10002) && (resultCode == Activity.RESULT_OK)){
            // recreate your fragment here
            finish();
            startActivity(getIntent());
        }
    }
}
