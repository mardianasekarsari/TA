package com.example.mardiana.alertsystemrumahpompa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DataRmhpompaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DataRmhpompaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataRmhpompaFragment extends Fragment implements SearchView.OnQueryTextListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static String[] rumah_pompa = new String[]{};
    private static String[] id = new String[]{};
    private static String[] jalan = new String[]{};

    private static String[] filter_rumah_pompa = new String[]{};
    private static String[] filter_id = new String[]{};
    private static String[] filter_jalan = new String[]{};

    private static final String[] STATUS_BANJIR = new String[] {
            "Semua", "Berpotensi Banjir", "Tidak Berpotensi Banjir"
    };

    private ArrayList<RumahPompa> rumahpompalist = new ArrayList<RumahPompa>();
    private ArrayList<RumahPompa> listbanjir = new ArrayList<RumahPompa>();
    private ArrayList<RumahPompa> listaman = new ArrayList<RumahPompa>();
    private ListView lv;
    private FloatingActionButton fab_add;
    private SearchView searchView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String apikey;

    Volley mVolleyService;
    Context mContext;

    private OnFragmentInteractionListener mListener;

    public DataRmhpompaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DataRmhpompaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DataRmhpompaFragment newInstance(String param1, String param2) {
        DataRmhpompaFragment fragment = new DataRmhpompaFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_data_rmhpompa, container, false);

        mContext = getActivity().getApplicationContext();
        mVolleyService = new Volley(mContext);

        SharedPreferences token = getActivity().getSharedPreferences(AppConfig.PREF_APIKEY, MODE_PRIVATE);
        apikey = token.getString("apikey", "");

        //getAllRumahPompa();

        lv = (ListView)rootView.findViewById(R.id.lv_rumahpompa);
        searchView=(SearchView) rootView.findViewById(R.id.searchView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, STATUS_BANJIR);
        final Spinner spinner_status = (Spinner)
                rootView.findViewById(R.id.spin_status_spinner);
        spinner_status.setAdapter(adapter);

        spinner_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String status = spinner_status.getSelectedItem().toString();
                if (status.equals("Semua")){
                    getAllRumahPompa();
                }
                else {
                    showbyStatus(status);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        lv.setTextFilterEnabled(true);
        setupSearchView();

        fab_add = ((FloatingActionButton) rootView.findViewById(R.id.fab_add_rumahpompa));

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddRumahPompaActivity.class);
                startActivityForResult(intent, 10004);
                //startActivity(intent);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView rumahpompa = (TextView)view.findViewById(R.id.tv_namarumahpompa);
                String nama_rumahpompa = rumahpompa.getText().toString();
                getRumahPompabyName(nama_rumahpompa);
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void setupSearchView()
    {
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Masukkan Nama Rumah Pompa");
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

    /*private void getAllRumahPompa(){
        String tag_string_req = "req_getallrumahpompa";
        //showProgress(true);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GETRUMAHPOMPA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Toast.makeText(getActivity().getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                //showProgress(false);
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray result = jObj.getJSONArray("result");
                    rumah_pompa = new String[result.length()];
                    id = new String[result.length()];
                    jalan = new String[result.length()];
                    ArrayList<RumahPompa> temp = new ArrayList<RumahPompa>();
                    for (int i=1; i<result.length(); i++){
                        JSONObject re = result.getJSONObject(i);
                        rumah_pompa[i] = re.getString("nama_");
                        id[i] = re.getString("id_rumah_pompa");
                        jalan[i] = re.getString("jalan");

                        RumahPompa rumahpompa = new RumahPompa();
                        rumahpompa.setNama(rumah_pompa[i]);
                        rumahpompa.setAlamat(jalan[i]);
                        temp.add(rumahpompa);
                    }
                    rumahpompalist = temp;
                    Collections.sort(rumahpompalist, new Comparator<RumahPompa>() {
                        @Override
                        public int compare(RumahPompa a1, RumahPompa a2) {
                            return (a1.nama.toString()).compareToIgnoreCase(a2.nama.toString());
                        }
                    });
                    lv.setAdapter(new RumahPompaAdapter(getActivity(), rumahpompalist));

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
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //showProgress(false);
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }*/

    private void getAllRumahPompa(){
        mVolleyService.getAll(AppConfig.URL_RUMAHPOMPA, apikey, new VolleyResponseListener() {
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
                        rumah_pompa = new String[result.length()];
                        id = new String[result.length()];
                        jalan = new String[result.length()];
                        ArrayList<RumahPompa> temp = new ArrayList<RumahPompa>();
                        for (int i=1; i<result.length(); i++){
                            JSONObject re = result.getJSONObject(i);
                            rumah_pompa[i] = re.getString("nama_");
                            id[i] = re.getString("id_rumah_pompa");
                            jalan[i] = re.getString("jalan");

                            RumahPompa rumahpompa = new RumahPompa();
                            rumahpompa.setNama(rumah_pompa[i]);
                            rumahpompa.setAlamat(jalan[i]);
                            temp.add(rumahpompa);
                        }
                        rumahpompalist = temp;
                        Collections.sort(rumahpompalist, new Comparator<RumahPompa>() {
                            @Override
                            public int compare(RumahPompa a1, RumahPompa a2) {
                                return (a1.nama.toString()).compareToIgnoreCase(a2.nama.toString());
                            }
                        });
                        lv.setAdapter(new RumahPompaAdapter(getActivity(), rumahpompalist));
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

    private void getRumahPompabyName(final String name) {
        mVolleyService.getrumahpompabyName(name, apikey, new VolleyResponseListener() {
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

                        String id = result.getString("id_rumah_pompa");

                        Intent intent = new Intent(getActivity().getApplicationContext(), RumahPompaActivity.class);
                        // sending data to new activity
                        intent.putExtra("id", id);
                        startActivity(intent);
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

    /*private void getRumahPompabyName(final String name){
        String tag_string_req = "req_getrumahpompabyname";
        //showProgress(true);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETRUMAHPOMPABYNAME, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //
                //showProgress(false);
                try {
                    //
                    JSONObject jObj = new JSONObject(response);
                    JSONObject result = jObj.getJSONObject("result");
                    String id = result.getString("id_rumah_pompa");

                    Intent intent = new Intent(getActivity().getApplicationContext(), RumahPompaActivity.class);
                    // sending data to new activity
                    intent.putExtra("id", id);
                    startActivity(intent);


                } catch (JSONException e) {
                    //JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "GetData Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //showProgress(false);
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("rumah_pompa", name);

                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }*/

    private void showbyStatus(final String status) {
        String url = AppConfig.URL_RUMAHPOMPA + status + "/status";

        mVolleyService.showbyStatus(status, apikey, new VolleyResponseListener() {
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

                        ArrayList<RumahPompa> temp = new ArrayList<RumahPompa>();
                        String alert = "";
                        filter_rumah_pompa = new String[result.length()];
                        filter_id = new String[result.length()];
                        filter_jalan = new String[result.length()];
                        for (int i=0; i<result.length(); i++){
                            JSONObject re = result.getJSONObject(i);
                            filter_rumah_pompa[i] = re.getString("nama_");
                            filter_id[i] = re.getString("id_rumah_pompa");
                            filter_jalan[i] = re.getString("jalan");
                            alert = re.getString("alert");

                            if (!filter_id[i].equals("0")){
                                RumahPompa rumahpompa = new RumahPompa();
                                rumahpompa.setNama(filter_rumah_pompa[i]);
                                rumahpompa.setAlamat(filter_jalan[i]);
                                temp.add(rumahpompa);
                            }

                        }
                        if (alert.equals("t")){
                            listbanjir = temp;
                            Collections.sort(listbanjir, new Comparator<RumahPompa>() {
                                @Override
                                public int compare(RumahPompa a1, RumahPompa a2) {
                                    return (a1.nama.toString()).compareToIgnoreCase(a2.nama.toString());
                                }
                            });
                            lv.setAdapter(new RumahPompaAdapter(getActivity(), listbanjir));
                        }else {
                            listaman = temp;
                            Collections.sort(listaman, new Comparator<RumahPompa>() {
                                @Override
                                public int compare(RumahPompa a1, RumahPompa a2) {
                                    return (a1.nama.toString()).compareToIgnoreCase(a2.nama.toString());
                                }
                            });
                            lv.setAdapter(new RumahPompaAdapter(getActivity(), listaman));
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

    /*private void showbyStatus(final String status){
        String tag_string_req = "req_getrumahpompabyname";
        //showProgress(true);
        Toast.makeText(mContext, AppConfig.URL_RUMAHPOMPA+"?status="+status, Toast.LENGTH_SHORT).show();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GETRUMAHPOMPABYSTATUS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //
                //showProgress(false);
                try {
                    //
                    JSONObject jObj = new JSONObject(response);
                    JSONArray result = jObj.getJSONArray("result");

                    ArrayList<RumahPompa> temp = new ArrayList<RumahPompa>();
                    String alert = "";
                    filter_rumah_pompa = new String[result.length()];
                    filter_id = new String[result.length()];
                    filter_jalan = new String[result.length()];
                    for (int i=0; i<result.length(); i++){
                        JSONObject re = result.getJSONObject(i);
                        filter_rumah_pompa[i] = re.getString("nama_");
                        filter_id[i] = re.getString("id_rumah_pompa");
                        filter_jalan[i] = re.getString("jalan");
                        alert = re.getString("alert");

                        if (!filter_id[i].equals("0")){
                            RumahPompa rumahpompa = new RumahPompa();
                            rumahpompa.setNama(filter_rumah_pompa[i]);
                            rumahpompa.setAlamat(filter_jalan[i]);
                            temp.add(rumahpompa);
                        }

                    }
                    if (alert.equals("t")){
                        listbanjir = temp;
                        Collections.sort(listbanjir, new Comparator<RumahPompa>() {
                            @Override
                            public int compare(RumahPompa a1, RumahPompa a2) {
                                return (a1.nama.toString()).compareToIgnoreCase(a2.nama.toString());
                            }
                        });
                        lv.setAdapter(new RumahPompaAdapter(getActivity(), listbanjir));
                    }else {
                        listaman = temp;
                        Collections.sort(listaman, new Comparator<RumahPompa>() {
                            @Override
                            public int compare(RumahPompa a1, RumahPompa a2) {
                                return (a1.nama.toString()).compareToIgnoreCase(a2.nama.toString());
                            }
                        });
                        lv.setAdapter(new RumahPompaAdapter(getActivity(), listaman));
                    }
                } catch (JSONException e) {
                    //JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "GetData Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //showProgress(false);
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", status);

                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }*/

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (TextUtils.isEmpty(newText)) {
            lv.clearTextFilter();
        } else {
            lv.setFilterText(newText);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10004) && (resultCode == Activity.RESULT_OK)){
            // recreate your fragment here
            DataRmhpompaFragment frg = new DataRmhpompaFragment();
            //(DataUserFragment) getFragmentManager().findFragmentById(R.id.fragment_datauser);
            getFragmentManager().beginTransaction()
                    .detach(frg)
                    .attach(frg)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.rumahpompalist.clear();
        getAllRumahPompa();
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
