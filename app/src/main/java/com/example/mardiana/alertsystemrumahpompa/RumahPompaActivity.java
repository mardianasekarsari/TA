package com.example.mardiana.alertsystemrumahpompa;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import android.os.Handler;
import java.util.TimerTask;
import java.util.logging.LogRecord;

public class RumahPompaActivity extends AppCompatActivity {

    private SessionManager session;
    private static String nama="";
    private static String alamat="";
    private static String nohp="";
    private static String threshold="";
    private String id_rumahpompa, nama_rumahpompa;
    private String role, apikey;
    Context mContext;
    Volley mVolleyService;
    Timer timer = new Timer();
    TimerTask updateProfile = new CustomTimerTask(this);

    private TextView tv_rumahpompa_nama, tv_rumahpompa_alamat, tv_rumahpompa_nohp,
            tv_rumahpompa_threshold, tv_rumahpompa_tinggiair, tv_rumahpompa_cuaca, tv_rumahpompa_pop,
            tv_rumahpompa_latitude, tv_rumahpompa_longitude, tv_rumahpompa_status, tv_rumahpompa_kedalamansaluran, tv_pompa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rumah_pompa);

        mContext = this;
        mVolleyService = new Volley(this);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Rumah Pompa");
        //getSupportActionBar().setHomeButtonEnabled(false);

        Intent intent = getIntent();
        id_rumahpompa = intent.getStringExtra("id");
        nama_rumahpompa = intent.getStringExtra("rumahpompa");
        
        session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = session.getUser();

        SharedPreferences token = getSharedPreferences(AppConfig.PREF_APIKEY, MODE_PRIVATE);
        apikey = token.getString("apikey", "");

        final String username = user.get(SessionManager.KEY_USERNAME);
        role = user.get(SessionManager.KEY_ROLE);

        tv_rumahpompa_nama = ((TextView) findViewById(R.id.tv_rumahpompa_namarmhpmp));
        tv_rumahpompa_alamat = ((TextView) findViewById(R.id.tv_rumahpompa_alamatrmhpmp));
        tv_rumahpompa_nohp = ((TextView) findViewById(R.id.tv_rumahpompa_nohprmhpmp));
        tv_rumahpompa_threshold = ((TextView) findViewById(R.id.tv_rumahpompa_threshold));
        tv_rumahpompa_tinggiair = ((TextView) findViewById(R.id.tv_rumahpompa_tinggirmhpmp));
        tv_rumahpompa_cuaca = ((TextView) findViewById(R.id.tv_rumahpompa_cuaca));
        tv_rumahpompa_pop = ((TextView) findViewById(R.id.tv_rumahpompa_pop));
        tv_rumahpompa_latitude = ((TextView) findViewById(R.id.tv_rumahpompa_latitude));
        tv_rumahpompa_longitude = ((TextView) findViewById(R.id.tv_rumahpompa_longitude));
        tv_rumahpompa_status = ((TextView) findViewById(R.id.tv_rumahpompa_statusalert));
        tv_rumahpompa_kedalamansaluran = ((TextView) findViewById(R.id.tv_rumahpompa_kedalamansaluran));
        tv_pompa = ((TextView) findViewById(R.id.tv_rumahpompa_pompa));
        Button btn_viewlog = ((Button) findViewById(R.id.btn_viewlog));


        //if (role.equals(AppConfig.PENGAWAS)){
        /*getrumahpompabyId(id_rumahpompa);
        getData(id_rumahpompa);*/
        /*}else if (role.equals(AppConfig.ADMIN)){
            getrumahpompabyId(id_rumahpompa);
            getData(id_rumahpompa);
        }*/
        refresh();
        timer.scheduleAtFixedRate(updateProfile, 0, 300000);

        btn_viewlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LogActivity.class);
                intent.putExtra("rumahpompa", id_rumahpompa);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private void refresh(){
        getrumahpompabyId(id_rumahpompa);
        getData(id_rumahpompa);
    }

    public class CustomTimerTask extends TimerTask {


        private Context context;
        private Handler mHandler = new Handler();

        public CustomTimerTask(Context con) {
            this.context = con;
        }



        @Override
        public void run() {
            new Thread(new Runnable() {

                public void run() {

                    mHandler.post(new Runnable() {
                        public void run() {
                            refresh();
                        }
                    });
                }
            }).start();

        }

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
                        nama = result.getString("nama_");
                        alamat = result.getString("jalan");
                        nohp = result.getString("no_telp_rumah_pompa");
                        threshold = result.getString("threshold_tinggi_air");
                        String latitude = result.getString("latitude");
                        String longitude = result.getString("longitude");
                        String statusalert = result.getString("alert");
                        String kedalamansaluran = result.getString("ketinggian_sungai");
                        String statuspompa = result.getString("status_pompa");

                        tv_rumahpompa_nama.setText(nama);
                        tv_rumahpompa_alamat.setText(alamat);
                        tv_rumahpompa_nohp.setText(nohp);
                        tv_rumahpompa_threshold.setText(threshold);
                        tv_rumahpompa_latitude.setText(latitude);
                        tv_rumahpompa_longitude.setText(longitude);
                        tv_rumahpompa_kedalamansaluran.setText(kedalamansaluran);

                        if (statusalert.equals("t"))
                            tv_rumahpompa_status.setText("Berpotensi Banjir");
                        else
                            tv_rumahpompa_status.setText("Tidak Berpotensi Banjir");

                        if (statuspompa.equals("t"))
                            tv_pompa.setText("Menyala");
                        else
                            tv_pompa.setText("Mati");
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

    /*private void getrumahpompabyId(final String id) {
        String tag_string_req = "req_rumahpompa";
        //showProgress(true);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETRUMAHPOMPABYID, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //showProgress(false);
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();

                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject result = jObj.getJSONObject("result");

                    nama = result.getString("nama_");
                    alamat = result.getString("jalan");
                    nohp = result.getString("no_telp_rumah_pompa");
                    threshold = result.getString("threshold_tinggi_air");
                    String latitude = result.getString("latitude");
                    String longitude = result.getString("longitude");
                    String status = result.getString("alert");
                    String kedalamansaluran = result.getString("ketinggian_sungai");

                    tv_rumahpompa_nama.setText(nama);
                    tv_rumahpompa_alamat.setText(alamat);
                    tv_rumahpompa_nohp.setText(nohp);
                    tv_rumahpompa_threshold.setText(threshold);
                    tv_rumahpompa_latitude.setText(latitude);
                    tv_rumahpompa_longitude.setText(longitude);
                    tv_rumahpompa_kedalamansaluran.setText(kedalamansaluran);

                    if (status.equals("t"))
                        tv_rumahpompa_status.setText("Berpotensi Banjir");
                    else
                        tv_rumahpompa_status.setText("Tidak Berpotensi Banjir");

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

    private void getData(final String id){
        String url = AppConfig.URL_DATA+ id;
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
                        String cuaca = result.getString("cuaca");
                        String pop = result.getString("chanceofrain");
                        String waktu = result.getString("waktu");
                        String ketinggianair = result.getString("ketinggian_air");

                        //check if rumahpompa has data
                        if (result.length()==0){
                            cuaca = "-";
                            pop = "-";
                            ketinggianair = "-";
                        }

                        tv_rumahpompa_cuaca.setText(cuaca);
                        tv_rumahpompa_tinggiair.setText(ketinggianair);
                        tv_rumahpompa_pop.setText(pop);
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

    /*private void getData(final String id){
        String tag_string_req = "req_getdata";
        //showProgress(true);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETDATA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Toast.makeText(getActivity().getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                //showProgress(false);
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject data = jObj.getJSONObject("result");

                    String cuaca = data.getString("cuaca");
                    String pop = data.getString("chanceofrain");
                    String waktu = data.getString("waktu");
                    String ketinggianair = data.getString("ketinggian_air");

                    //check if rumahpompa has data
                    if (data.length()==0){
                        cuaca = "-";
                        pop = "-";
                        ketinggianair = "-";
                    }

                    tv_rumahpompa_cuaca.setText(cuaca);
                    tv_rumahpompa_tinggiair.setText(ketinggianair);
                    tv_rumahpompa_pop.setText(pop);


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
        }){

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_rumahpompa, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_rumahpompa, menu);
        if (role.equals(AppConfig.PENGAWAS))
        {
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        }

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

    private void edit(){
        Intent intent = new Intent(this, EditRumahPompaActivity.class);
        intent.putExtra("id", id_rumahpompa);
        startActivity(intent);
    }

    private void confirmDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RumahPompaActivity.this);
        builder.setTitle("Hapus Data");
        builder.setCancelable(true);
        builder.setMessage("Apakah Anda Yakin Untuk Menghapus Data?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int ids) {
                delete(id_rumahpompa);
            }
        });
        builder.setNegativeButton("NO", null);
        builder.show();
    }

    private void delete(String idrumahpompa){
        String url = AppConfig.URL_RUMAHPOMPA + idrumahpompa;
        mVolleyService.delete(url, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public ArrayList<String> onResponse(JSONObject response) {
                try {
                    boolean status = response.getBoolean("status");
                    if (status){
                        Toast.makeText(RumahPompaActivity.this, AppConfig.DELETE_SUCCESS, Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        String kode = response.getString("kode");
                        if (kode.equals("1")){
                            Toast.makeText(RumahPompaActivity.this, AppConfig.DELETE_FAILED, Toast.LENGTH_SHORT).show();
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

    /*private void delete(final String id){
        String tag_string_req = "req_deleterumahpompa";
        //showProgress(true);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_DELETERUMAHPOMPA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //
                //showProgress(false);
                try {
                    //
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                    if (status){
                        Toast.makeText(RumahPompaActivity.this, AppConfig.DELETE_SUCCESS, Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(RumahPompaActivity.this, AppConfig.DELETE_FAILED, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    //JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "GetData Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //showProgress(false);
            }
        }){

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

}
