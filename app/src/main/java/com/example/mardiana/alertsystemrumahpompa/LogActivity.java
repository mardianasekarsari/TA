package com.example.mardiana.alertsystemrumahpompa;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class LogActivity extends AppCompatActivity {
    Context mContext;
    Volley mVolleyService;
    private String apikey, rumahpompa;
    private HashMap<String, String> listUser = new HashMap<String, String>();
    private ArrayList<Log> logList = new ArrayList<Log>();
    private ListView lv_log;
    TextView tv_lognodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        mContext = this;
        mVolleyService = new Volley(mContext);

        SharedPreferences token = getSharedPreferences(AppConfig.PREF_APIKEY, MODE_PRIVATE);
        apikey = token.getString("apikey", "");

        tv_lognodata = ((TextView) findViewById(R.id.tv_lognodata));

        Intent intent = getIntent();
        rumahpompa = intent.getStringExtra("rumahpompa");

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Catatan");

        lv_log = ((ListView) findViewById(R.id.lv_log));

        getAllUser();

        lv_log.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv_nama = (TextView)view.findViewById(R.id.tv_logusername);
                TextView tv_log = (TextView)view.findViewById(R.id.tv_log);
                TextView tv_waktu = (TextView)view.findViewById(R.id.tv_logwaktu);
                String nama = tv_nama.getText().toString();
                String log = tv_log.getText().toString();
                String waktu = tv_waktu.getText().toString();
                showDetail(nama, waktu, log);
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

    private void getLog(final String id) {
        String url = AppConfig.URL_LOG + id;
        mVolleyService.getBy(url, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public ArrayList<String> onResponse(JSONObject response) {
                try {
                    boolean status = response.getBoolean("status");
                    ArrayList<Log> temp = new ArrayList<Log>();
                    if (status) {

                        JSONArray result = response.getJSONArray("result");
                        if (result.length()!=0){
                            tv_lognodata.setVisibility(View.GONE);
                            for (int i=0; i<result.length(); i++){
                                JSONObject re = result.getJSONObject(i);
                                String username = re.getString("username");
                                String waktu = re.getString("waktu");
                                String isilog = re.getString("keterangan");
                                String nama = listUser.get(username);
                                String tahun = waktu.substring(0,4);
                                String angkabulan = waktu.substring(5,7);
                                String tanggal = waktu.substring(8,10);
                                String jam = waktu.substring(11,16);
                                String bulan=" ";

                                switch(angkabulan) {
                                    case "01" :
                                        bulan = "Januari";
                                        break;
                                    case "02" :
                                        bulan = "Februari";
                                        break;
                                    case "03" :
                                        bulan = "Maret";
                                        break;
                                    case "04" :
                                        bulan = "April";
                                        break;
                                    case "05" :
                                        bulan = "Mei";
                                        break;
                                    case "06" :
                                        bulan = "Juni";
                                        break;
                                    case "07" :
                                        bulan = "Juli";
                                        break;
                                    case "08" :
                                        bulan = "Agustus";
                                        break;
                                    case "09" :
                                        bulan = "September";
                                        break;
                                    case "10" :
                                        bulan = "Oktober";
                                        break;
                                    case "11" :
                                        bulan = "November";
                                        break;
                                    case "12" :
                                        bulan = "Desmber";
                                        break;

                                }

                                String waktufix = tanggal + " " + bulan + " " + tahun + "  " + jam;

                                Log log = new Log();
                                log.setUsername(username);
                                log.setWaktu(waktufix);
                                log.setNama(nama);
                                log.setLog(isilog);
                                temp.add(log);
                            }
                            logList = temp;
                            Collections.sort(logList, new Comparator<Log>() {
                                @Override
                                public int compare(Log a1, Log a2) {
                                    return (a1.waktu.toString()).compareToIgnoreCase(a2.waktu.toString());
                                }
                            });
                            lv_log.setAdapter(new LogAdapter(LogActivity.this, logList));
                        }else {
                            tv_lognodata.setVisibility(View.VISIBLE);
                        }
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

    private void getAllUser(){
        mVolleyService.getAll(AppConfig.URL_USER, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public ArrayList<String> onResponse(JSONObject response) {
                try {
                    boolean status = response.getBoolean("status");
                    if (status){
                        JSONArray result = response.getJSONArray("result");
                        for (int i=1; i<result.length(); i++){
                            JSONObject re = result.getJSONObject(i);
                            String username = re.getString("username");
                            String nama = re.getString("nama_user");
                            listUser.put(username, nama);
                        }
                        getLog(rumahpompa);
                    }
                    else {
                        Toast.makeText(mContext, getString(R.string.invalid_token), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    private void showDetail(String username, String waktu, String log) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_detail_log, null);
        dialogBuilder.setView(dialogView);

        final TextView tv_username = (TextView) dialogView.findViewById(R.id.tv_detaillogusername);
        final TextView tv_waktu = (TextView) dialogView.findViewById(R.id.tv_detaillogwaktu);
        final TextView tv_log = (TextView) dialogView.findViewById(R.id.tv_detaillog);

        tv_username.setText(username);
        tv_waktu.setText(waktu);
        tv_log.setText(log);

        dialogBuilder.setTitle("Detail Catatan");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}
