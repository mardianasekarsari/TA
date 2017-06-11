package com.example.mardiana.alertsystemrumahpompa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RumahPompaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RumahPompaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RumahPompaFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private GoogleMap mMap;
    MapView mMapView;
    private static String[] rumah_pompa = new String[]{};
    private static String[] id = new String[]{};

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static String nama="";
    private static String alamat="";
    private static String nohp="";
    private static String threshold="";
    private static String kedalamaninlet="";
    private static String data_cuaca="";
    private static String data_pop="";
    private static String data_tinggiair="";
    private String apikey;

    private Bitmap mFinalBitmap;
    private int mColorCode;

    Map<Integer, String> last_data_cuaca = new HashMap<Integer, String>();
    Map<Integer, String> last_data_tinggiair = new HashMap<Integer, String>();
    Map<Integer, String> last_data_pop = new HashMap<Integer, String>();

    private SessionManager session;
    private LoginSQLiteHandler db;

    private TextView tv_rumahpompa_nama, tv_rumahpompa_alamat, tv_rumahpompa_nohp, tv_rumahpompa_threshold,
            tv_rumahpompa_tinggiair, tv_rumahpompa_cuaca, tv_rumahpompa_pop, tv_rumahpompa_status, tv_rumahpompa_kedalamaninlet, tv_tgl,
            tv_pompa;
    private FloatingActionButton btn_edit;
    private ImageButton btn_edit_threshold;
    private CoordinatorLayout cl_rumahpompa;
    private AutoCompleteTextView ac_rumahpompa;
    private ImageButton btn_clear;

    List<Marker> markersList = new ArrayList<Marker>();
    final List<String> list_rumahpompa = new ArrayList<String>();
    LatLngBounds.Builder builder;
    CameraUpdate cu;
    String idrumahpompa, role;
    SwitchCompat switch_pompa;

    Volley mVolleyService;
    Context mContext;


    private OnFragmentInteractionListener mListener;

    public RumahPompaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RumahPompaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RumahPompaFragment newInstance(String param1, String param2) {
        RumahPompaFragment fragment = new RumahPompaFragment();
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
        View view = inflater.inflate(R.layout.fragment_rumah_pompa, container, false);

        mContext = getActivity().getApplicationContext();
        mVolleyService = new Volley(mContext);

        db = new LoginSQLiteHandler(getActivity());
        session = new SessionManager(getActivity().getApplicationContext());

        SharedPreferences token = getActivity().getSharedPreferences(AppConfig.PREF_APIKEY, MODE_PRIVATE);
        apikey = token.getString("apikey", "");

        getLastData();

        tv_rumahpompa_nama = ((TextView) view.findViewById(R.id.tv_petugas_namarmhpmp));
        tv_rumahpompa_alamat = ((TextView) view.findViewById(R.id.tv_petugas_alamatrmhpmp));
        tv_rumahpompa_nohp = ((TextView) view.findViewById(R.id.tv_petugas_nohprmhpmp));
        tv_rumahpompa_threshold = ((TextView) view.findViewById(R.id.tv_petugas_threshold));
        tv_rumahpompa_tinggiair = ((TextView) view.findViewById(R.id.tv_petugas_tinggirmhpmp));
        tv_rumahpompa_cuaca = ((TextView) view.findViewById(R.id.tv_petugas_cuaca));
        tv_rumahpompa_pop = ((TextView) view.findViewById(R.id.tv_petugas_pop));
        tv_rumahpompa_status = ((TextView) view.findViewById(R.id.tv_petugas_status));
        tv_rumahpompa_kedalamaninlet = ((TextView) view.findViewById(R.id.tv_petugas_kedalamansaluran));
        tv_pompa = ((TextView) view.findViewById(R.id.tv_petugas_pompa));
        tv_tgl = ((TextView) view.findViewById(R.id.tv_tgl));
        cl_rumahpompa = ((CoordinatorLayout) view.findViewById(R.id.rumahpompa));
        ac_rumahpompa = ((AutoCompleteTextView) view.findViewById(R.id.ac_rumahpompa));
        switch_pompa = ((SwitchCompat) view.findViewById(R.id.switch_pompa_status));

        switch_pompa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    editStatusPompa("true");
                    tv_pompa.setText("Menyala");
                }else {
                    editStatusPompa("false");
                    tv_pompa.setText("Mati");
                }
            }
        });

        Date now = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE, dd MMMM yyyy", new java.util.Locale("id")); // the day of the week spelled out completely
        String day = simpleDateformat.format(now);

        tv_tgl.setText(day);

        HashMap<String, String> user = session.getUser();

        final String username = user.get(SessionManager.KEY_USERNAME);
        String nama = user.get(SessionManager.KEY_NAME);
        String nohp = user.get(SessionManager.KEY_PHONE);
        String alamat = user.get(SessionManager.KEY_ADDRESS);
        idrumahpompa = user.get(SessionManager.KEY_RUMAHPOMPAID);
        role = user.get(SessionManager.KEY_ROLE);

        if (role.equals("PENGAWAS")){
            cl_rumahpompa.setVisibility(View.GONE);
            switch_pompa.setVisibility(View.GONE);
            mMapView = (MapView) view.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);

            mMapView.onResume(); // needed to get the map to display immediately

            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    mMap = map;

                    // For showing a move to my location button
                    //mMap.setMyLocationEnabled(true);

                    // For dropping a marker at a point on the Map
                    getAllRumahPompa();

                    // For zooming automatically to the location of the marker
                    /*CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
                }
            });
        }
        else if (role.equals("PETUGAS")){

        }

        getrumahpompabyId(idrumahpompa);
        getData(idrumahpompa);

        btn_edit_threshold = ((ImageButton) view.findViewById(R.id.btn_edit_threshold));
        btn_edit_threshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditRumahPompaActivity.class);
                getActivity().startActivity(intent);
            }
        });

        btn_clear = ((ImageButton) view.findViewById(R.id.btn_clear));
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ac_rumahpompa.setText("");
            }
        });

        //getCuaca();
        /*btn_edit = ((FloatingActionButton) view.findViewById(R.id.fab_rumahpompa_edit));
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditRumahPompaActivity.class);
                getActivity().startActivity(intent);
            }
        });*/

        return view;
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
                        kedalamaninlet = result.getString("ketinggian_sungai");
                        String statusalert = result.getString("alert");
                        String statuspompa = result.getString("status_pompa");

                        tv_rumahpompa_nama.setText(nama);
                        tv_rumahpompa_alamat.setText(alamat);
                        tv_rumahpompa_nohp.setText(nohp);
                        tv_rumahpompa_threshold.setText(threshold);
                        tv_rumahpompa_kedalamaninlet.setText(kedalamaninlet);

                        if (statusalert.equals("t")){
                            tv_rumahpompa_status.setText("Berpotensi Banjir");
                        }
                        else {
                            tv_rumahpompa_status.setText("Tidak Berpotensi Banjir");
                        }

                        if (statuspompa.equals("t")){
                            switch_pompa.setChecked(true);
                            tv_pompa.setText("Menyala");
                        }
                        else {
                            switch_pompa.setChecked(false);
                            tv_pompa.setText("Mati");
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
                        if (!result.isNull("id_data")){
                            String cuaca = result.getString("cuaca");
                            String pop = result.getString("chanceofrain");
                            String waktu = result.getString("waktu");
                            String ketinggianair = result.getString("ketinggian_air");

                            tv_rumahpompa_cuaca.setText(cuaca);
                            tv_rumahpompa_tinggiair.setText(ketinggianair);
                            tv_rumahpompa_pop.setText(pop);
                        }
                        else {
                            //Toast.makeText(getActivity().getApplicationContext(), AppConfig.NODATA , Toast.LENGTH_SHORT).show();
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
                        final JSONArray result = response.getJSONArray("result");

                        rumah_pompa = new String[result.length()];
                        id = new String[result.length()];

                        final HashMap<Marker, String> markerMap = new HashMap<Marker, String>();
                        final HashMap<String, Marker> markerName = new HashMap<String, Marker>();

                        for (int i=1; i<result.length(); i++){
                            JSONObject re = result.getJSONObject(i);
                            rumah_pompa[i] = re.getString("nama_");
                            id[i] = re.getString("id_rumah_pompa");
                            String latitude = re.getString("latitude");
                            String longitude = re.getString("longitude");
                            String alert = re.getString("alert");

                            //Add to list autocomplete
                            list_rumahpompa.add(rumah_pompa[i]);
                            //Collections.sort(list_rumahpompa);

                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                                    R.layout.item_search, list_rumahpompa);
                            //dataAdapter.setDropDownViewResource(layoutItemId);
                            ac_rumahpompa.setThreshold(1);

                            dataAdapter.sort(new Comparator<String>() {
                                @Override
                                public int compare(String lhs, String rhs) {
                                    return lhs.compareTo(rhs);
                                }
                            });

                            ac_rumahpompa.setAdapter(dataAdapter);

                            data_cuaca = last_data_cuaca.get(i);
                            data_tinggiair = last_data_tinggiair.get(i);
                            data_pop = last_data_pop.get(i);

                            Drawable sourceDrawable = getResources().getDrawable(R.drawable.ic_marker);
                            Bitmap sourceBitmap = convertDrawableToBitmap(sourceDrawable);

                            Marker rumahpompa = null;
                            if (alert.equals("t")){
                                mColorCode = Color.parseColor("#ff0000");
                                mFinalBitmap = changeImageColor(sourceBitmap, mColorCode);
                                int newWidth = (int) (mFinalBitmap.getWidth() * 1.5);
                                int newHeight = (int) (mFinalBitmap.getHeight() * 1.5);
                                Bitmap resizedBitmap = Bitmap.createScaledBitmap(mFinalBitmap, newWidth, newHeight, true);
                                //Bitmap resizedBitmap = Bitmap.createScaledBitmap(mFinalBitmap, 150, 150, false);
                                rumahpompa = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)))
                                        .title(rumah_pompa[i])
                                        .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));
                                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            }
                            else if (alert.equals("f")){
                                mColorCode = Color.parseColor("#32CD32");
                                mFinalBitmap = changeImageColor(sourceBitmap, mColorCode);
                                int newWidth = (int) (mFinalBitmap.getWidth() * 1.5);
                                int newHeight = (int) (mFinalBitmap.getHeight() * 1.5);
                                Bitmap resizedBitmap = Bitmap.createScaledBitmap(mFinalBitmap, newWidth, newHeight, true);
                                //Bitmap resizedBitmap = Bitmap.createScaledBitmap(mFinalBitmap, 150, 150, false);
                                rumahpompa = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)))
                                        .title(rumah_pompa[i])
                                        .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));
                                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                            }

                            markerMap.put(rumahpompa, id[i]);
                            markerName.put(rumah_pompa[i], rumahpompa);
                            markersList.add(rumahpompa);

                            ac_rumahpompa.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                                    // TODO Auto-generated method stub
                                    String nama_rumahpompa = arg0.getItemAtPosition(arg2).toString();
                                    getRumahPompabyName(nama_rumahpompa);

                                    Marker mark = markerName.get(nama_rumahpompa);
                                    mark.showInfoWindow();
                                }
                            });
                        }

                        /*create for loop for get the latLngbuilder from the marker list*/
                        builder = new LatLngBounds.Builder();
                        for (Marker m : markersList) {
                            builder.include(m.getPosition());
                        }
                        /*initialize the padding for map boundary*/
                        int padding = 50;
                        /*create the bounds from latlngBuilder to set into map camera*/
                        final LatLngBounds bounds = builder.build();
                        /*create the camera with bounds and padding to set into map*/
                        cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        /*call the map call back to know map is loaded or not*/
                        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                            @Override
                            public void onMapLoaded() {
                            /*set animated zoom camera into map*/
                                mMap.animateCamera(cu);

                            }
                        });

                        // Setting a custom info window adapter for the google map
                        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                            // Use default InfoWindow frame
                            @Override
                            public View getInfoWindow(Marker arg0) {
                                return null;
                            }

                            // Defines the contents of the InfoWindow
                            @Override
                            public View getInfoContents(Marker marker) {

                                String id = markerMap.get(marker);

                                // Getting view from the layout file info_window_layout
                                View v = getActivity().getLayoutInflater().inflate(R.layout.infowindow, null);

                                // Set desired height and width
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(600, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutParams.setMargins(32, 32, 32, 32);
                                v.setLayoutParams(layoutParams);

                                //v.setLayoutParams(new RelativeLayout.LayoutParams(600, RelativeLayout.LayoutParams.WRAP_CONTENT));
                                LinearLayout ll_cuaca = (LinearLayout) v.findViewById(R.id.ll_infowindow_cuaca);
                                LinearLayout ll_air = (LinearLayout) v.findViewById(R.id.ll_infowindow_air);
                                LinearLayout ll_pop = (LinearLayout) v.findViewById(R.id.ll_infowindow_pop);
                                TextView tv_infowindow_title = (TextView) v.findViewById(R.id.tv_infowindow_title);
                                TextView tv_infowindow_cuaca = (TextView) v.findViewById(R.id.tv_infowindow_cuaca);
                                TextView tv_infowindow_tinggiair = (TextView) v.findViewById(R.id.tv_infowindow_tinggiair);
                                TextView tv_infowindow_pop = (TextView) v.findViewById(R.id.tv_infowindow_pop);
                                TextView tv_infowindow_nodata = ((TextView) v.findViewById(R.id.tv_infowindow_nodata));

                                if ((last_data_cuaca.get(Integer.valueOf(id))).equals("-")){
                                    tv_infowindow_title.setText(rumah_pompa[Integer.valueOf(id)]);
                                    tv_infowindow_nodata.setText(AppConfig.NODATA);
                                    ll_cuaca.setVisibility(View.GONE);
                                    ll_air.setVisibility(View.GONE);
                                    ll_pop.setVisibility(View.GONE);
                                }
                                else {
                                    tv_infowindow_nodata.setVisibility(View.GONE);
                                    tv_infowindow_title.setText(rumah_pompa[Integer.valueOf(id)]);
                                    tv_infowindow_cuaca.setText((last_data_cuaca.get(Integer.valueOf(id))));
                                    tv_infowindow_tinggiair.setText((last_data_tinggiair.get(Integer.valueOf(id))));
                                    tv_infowindow_pop.setText((last_data_pop.get(Integer.valueOf(id))));
                                }
                                // Returning the view containing InfoWindow contents
                                return v;

                            }
                        });

                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                String id = markerMap.get(marker);

                                Intent intent = new Intent(getContext(),RumahPompaActivity.class);
                                intent.putExtra("id", id);
                                startActivity(intent);
                            }
                        });
                    }
                    else {
                        //Token Invalid
                        Toast.makeText(mContext, getString(R.string.invalid_token), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    public  void getLastData(){
        mVolleyService.getAll(AppConfig.URL_DATA, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public ArrayList<String> onResponse(JSONObject response) {
                try {
                    Boolean status = response.getBoolean("status");
                    if (status){
                        JSONArray jArr = response.getJSONArray("result");
                        for (int i=0; i<jArr.length(); i++){
                            JSONObject re = jArr.getJSONObject(i);
                            int key = i;

                            if (re.isNull("id_data")){
                                last_data_cuaca.put(key, "-");
                                last_data_tinggiair.put(key, "-");
                                last_data_pop.put(key, "-");
                            }
                            else {
                                last_data_cuaca.put(key, re.getString("cuaca"));
                                last_data_tinggiair.put(key, re.getString("ketinggian_air"));
                                last_data_pop.put(key, re.getString("chanceofrain"));
                            }
                        }
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

    private void editStatusPompa (final String status){
        mVolleyService.editStatusPompa(idrumahpompa, status, apikey, new VolleyResponseListener() {
            @Override
            public ArrayList<String> onResponse(JSONObject response) {
                try {
                    Boolean status = response.getBoolean("status");
                    if (status){

                    }else{
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            public void onError(String message) {

            }
        });
    }

    private void getRumahPompabyName(final String name) {
        String url = AppConfig.URL_RUMAHPOMPA + name + "/name" ;
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
                        String latitude = result.getString("latitude");
                        String longitude = result.getString("longitude");

                        //Toast.makeText(getActivity().getApplicationContext(), latitude + " " + longitude, Toast.LENGTH_SHORT).show();
                        LatLng latlng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 15);
                        mMap.animateCamera(cameraUpdate);
                        //mMap.moveCamera(cameraUpdate);
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

    public static Bitmap changeImageColor(Bitmap sourceBitmap, int color) {
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
                sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        p.setColorFilter(filter);
        p.setTextSize(35);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
        return resultBitmap;
    }
    public static Drawable covertBitmapToDrawable(Context context, Bitmap bitmap) {
        Drawable d = new BitmapDrawable(context.getResources(), bitmap);
        return d;
    }
    public static Bitmap convertDrawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
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

