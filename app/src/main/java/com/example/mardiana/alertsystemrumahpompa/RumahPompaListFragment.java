package com.example.mardiana.alertsystemrumahpompa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RumahPompaListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RumahPompaListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RumahPompaListFragment extends Fragment implements SearchView.OnQueryTextListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static String[] rumah_pompa = new String[]{};
    private static String[] id = new String[]{};
    private static String[] jalan = new String[]{};

    private static String[] filter_rumah_pompa = new String[]{};
    private static String[] filter_id = new String[]{};
    private static String[] filter_jalan = new String[]{};

    private String apikey;

    private SearchView searchView;
    private Context mContext;
    private Volley mVolleyService;

    private ArrayList<RumahPompa> rumahpompalist = new ArrayList<RumahPompa>();
    private ArrayList<RumahPompa> listbanjir = new ArrayList<RumahPompa>();
    private ArrayList<RumahPompa> listaman = new ArrayList<RumahPompa>();
    private ListView lv;

    public RumahPompaListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RumahPompaListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RumahPompaListFragment newInstance(String param1, String param2) {
        RumahPompaListFragment fragment = new RumahPompaListFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_rumah_pompa_list, container, false);
        lv = (ListView)rootView.findViewById(R.id.lv_rumahpompa);

        mContext = getActivity().getApplicationContext();
        mVolleyService = new Volley(mContext);

        SharedPreferences token = getActivity().getSharedPreferences(AppConfig.PREF_APIKEY, MODE_PRIVATE);
        apikey = token.getString("apikey", "");

        //getAllRumahPompa();

        String[] values = new String[] {"Semua Rumah Pompa", "Berpotensi Banjir", "Tidak Berpotensi Banjir"};
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }
        searchView=(SearchView) rootView.findViewById(R.id.searchView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, values);
        final Spinner spinner_status = (Spinner)
                rootView.findViewById(R.id.spin_status_spinner);
        spinner_status.setAdapter(adapter);

        spinner_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String status = spinner_status.getSelectedItem().toString();
                /*Toast.makeText(getActivity().getApplicationContext(), status, Toast.LENGTH_SHORT).show();*/
                if (status.equals("Semua Rumah Pompa")){
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

    private void setupSearchView()
    {
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Masukkan Nama Rumah Pompa");
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

    private void showbyStatus(final String status) {
        mVolleyService.showbyStatus(status, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public ArrayList<String> onResponse(JSONObject response) {
                try {
                    Boolean status = response.getBoolean("status");
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
                        //Invalid Token
                        Toast.makeText(mContext, getString(R.string.invalid_token), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    private void getAllRumahPompa(){
        mVolleyService.getAll(AppConfig.URL_RUMAHPOMPA, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public ArrayList<String> onResponse(JSONObject response) {
                try {
                    Boolean status = response.getBoolean("status");
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
                        //Invalid Token
                        Toast.makeText(mContext, getString(R.string.invalid_token), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
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
            public ArrayList<String> onResponse(JSONObject response) {
                try {
                    Boolean status = response.getBoolean("status");
                    if (status){
                        JSONObject result = response.getJSONObject("result");
                        String id = result.getString("id_rumah_pompa");

                        Intent intent = new Intent(getActivity().getApplicationContext(), RumahPompaActivity.class);
                        // sending data to new activity
                        intent.putExtra("id", id);
                        startActivity(intent);
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

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (TextUtils.isEmpty(s)) {
            lv.clearTextFilter();
        } else {
            lv.setFilterText(s);
        }
        return true;
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
