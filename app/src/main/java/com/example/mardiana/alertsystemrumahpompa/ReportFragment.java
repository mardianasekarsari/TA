package com.example.mardiana.alertsystemrumahpompa;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import static java.util.logging.Logger.global;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReportFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Volley mVolleyService;
    Context mContext;
    String apikey;
    ArrayList<String> nama_rumahpompa = new ArrayList<String>();
    HashMap<String, String> listrumahpompa = new HashMap<String, String>();
    String nama_pompa;
    ArrayList<String> lala = new ArrayList<String>();
    ArrayList<BarEntry> report = new ArrayList<BarEntry>();
    String selectedmonth, selectedyear;
    Spinner spinner_month, spinner_year;

    private static String[] rumah_pompa = new String[]{};
    private static String[] id = new String[]{};
    BarChart chart_report;
    ArrayList<BarEntry> valueSet1 = new ArrayList<>();

    public ReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        mContext = getActivity().getApplicationContext();
        mVolleyService = new Volley(mContext);

        SharedPreferences token = getActivity().getSharedPreferences(AppConfig.PREF_APIKEY, 0);
        apikey = token.getString("apikey", "");

        spinner_month = ((Spinner) view.findViewById(R.id.spinner_month));
        spinner_year = ((Spinner) view.findViewById(R.id.spinner_year));
        chart_report = ((HorizontalBarChart) view.findViewById(R.id.chart_report));
        Button btn_download = ((Button) view.findViewById(R.id.btn_download));

        ArrayList<String> listyear = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i=2017; i<=thisYear; i++){
            listyear.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, listyear);
        spinner_year.setAdapter(adapter);

        String[] month = { "Januari", "Feburari", "Maret", "April", "Mei",
                "Juni", "Juli", "Agustus", "September", "Oktober", "November","Desember" };
        ArrayAdapter adapterMonth = new ArrayAdapter(
                getActivity(),android.R.layout.simple_dropdown_item_1line ,month);
        spinner_month.setAdapter(adapterMonth);

        Date now = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("MMMM", new java.util.Locale("id")); // the day of the week spelled out completely
        String current_month = simpleDateformat.format(now);

        spinner_month.setSelection(adapterMonth.getPosition(current_month));

        spinner_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedmonth = spinner_month.getSelectedItem().toString();
                selectedyear = spinner_year.getSelectedItem().toString();
                getAllRumahPompa(selectedmonth, selectedyear);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedmonth = spinner_month.getSelectedItem().toString();
                selectedyear = spinner_year.getSelectedItem().toString();
                getAllRumahPompa(selectedmonth, selectedyear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download(selectedmonth, selectedyear);
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void getAllRumahPompa(final String smonth, final String syear){
        mVolleyService.getAll(AppConfig.URL_RUMAHPOMPA, apikey, new VolleyResponseListener() {
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
                        rumah_pompa = new String[result.length()];
                        id = new String[result.length()];
                        listrumahpompa.clear();
                        for (int i=0; i<result.length(); i++){
                            JSONObject re = result.getJSONObject(i);
                            rumah_pompa[i] = re.getString("nama_");
                            id[i] = re.getString("id_rumah_pompa");
                            String nama;
                            if (rumah_pompa[i].length()>30){
                                nama = rumah_pompa[i].substring(11, 30);
                            }else {
                                nama = rumah_pompa[i].substring(11);
                            }
                            nama = "R.P " + nama;
                            listrumahpompa.put(id[i], nama);
                        }

                        getReportData(smonth, syear);
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

    private void getReportData(String month, String year){

        mVolleyService.getReport(apikey, month, year, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public ArrayList<String> onResponse(JSONObject response) {
                try {
                    boolean status = response.getBoolean("status");
                    if (status){
                        JSONArray data = response.getJSONArray("result");
                        nama_rumahpompa.clear();
                        report.clear();
                        for (int i=0; i<data.length(); i++){
                            JSONObject re = data.getJSONObject(i);
                            String count = re.getString("count");
                            String id_rumah_pompa = re.getString("id_rumah_pompa");
                            String nama = listrumahpompa.get(id_rumah_pompa);
                            if (Integer.valueOf(id_rumah_pompa)!=0){
                                nama_rumahpompa.add(nama);
                                report.add(new BarEntry(Integer.valueOf(count), i));
                            }
                        }
                    /*Log.e("nama rumpa", String.valueOf(nama_rumahpompa.size()));
                    Log.e("data rumpa", String.valueOf(report.size()));*/

                        //Toast.makeText(mContext, nama_rumahpompa.size(), Toast.LENGTH_SHORT).show();

                        BarDataSet dataset = new BarDataSet(report, "");
                        BarData dataalert = new BarData(nama_rumahpompa, dataset);

                        chart_report.setData(dataalert);
                        chart_report.animateY(5000);
                        chart_report.setDrawBarShadow(false);
                        chart_report.setDrawValueAboveBar(true);
                        chart_report.setDescription("");
                        chart_report.setNoDataText("Data Tidak Tersedia");
                        XAxis xAxis = chart_report.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setTextColor(Color.BLACK);
                    /* xAxis.setTypeface(mTf);*/
                        xAxis.setDrawGridLines(false);
                        xAxis.setSpaceBetweenLabels(50);
                        YAxis leftAxis = chart_report.getAxisLeft();
                        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        leftAxis.setSpaceTop(15f);
                        leftAxis.setTextColor(Color.BLACK);
                        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
                        YAxis rightAxis = chart_report.getAxisRight();
                        rightAxis.setDrawGridLines(false);
                        rightAxis.setEnabled(false);
                        //  rightAxis.setPosition(rightAxis.getLabelPosition());
                        rightAxis.setSpaceTop(15f);
                        rightAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
                        Legend l = chart_report.getLegend();
                        l.setEnabled(false);
                        chart_report.invalidate();
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

    public void download(String month, String year)
    {
        String url = AppConfig.URL_DATA + "pdf/" + month + "/" + year;
        String namafile = "Laporan Rumah Pompa "+ month + " " + year;
        new DownloadFile().execute(url, namafile);

    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {
        String fileName ;
        @Override
        protected Void doInBackground(String... strings) {

            String fileUrl = strings[0];
            fileName = strings[1]+".pdf";
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            /*Log.e("direktorinya", extStorageDirectory);
            File folder = new File(extStorageDirectory + "RumahPompa");*/
            if (!path.exists()) {
                path.mkdir();
            }

            File pdfFile = new File(path, fileName);

            try{
                pdfFile.createNewFile();
                Log.e("Download", "Berhasil");
            }catch (IOException e){
                e.printStackTrace();
                Log.e("Download", e.getMessage());
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(mContext, "Download Berhasil", Toast.LENGTH_SHORT).show();
            view(fileName);
            /*webview_report.getSettings().setJavaScriptEnabled(true);  //untuk mengaktifkan javascript
            webview_report.loadUrl(url);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);*/
            super.onPostExecute(aVoid);
        }
    }

    public void view(String namafile)
    {
        //String namafile = "Laporan Rumah Pompa "+ month + " " + year;
        File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" +namafile);  // -> filename = maven.pdf
        Uri path = Uri.fromFile(pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent intent = Intent.createChooser(pdfIntent, "Open File");

        try{
            startActivity(intent);
        }catch(ActivityNotFoundException e){
            Toast.makeText(getActivity(), "No Application available to view PDF", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
}