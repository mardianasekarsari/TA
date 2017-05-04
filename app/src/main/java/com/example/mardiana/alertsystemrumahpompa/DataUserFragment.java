package com.example.mardiana.alertsystemrumahpompa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DataUserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DataUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataUserFragment extends Fragment implements android.widget.SearchView.OnQueryTextListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ArrayList<User> userlist = new ArrayList<User>();
    private ListView lv;
    Context mContext;
    Volley mVolleyService;

    private String rumahpompa;
    SearchView searchView;
    String apikey;

    Map<String, String> user_rumahpompa = new HashMap<String, String>();


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DataUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DataUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DataUserFragment newInstance(String param1, String param2) {
        DataUserFragment fragment = new DataUserFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_data_petugas, container, false);

        mContext = getActivity().getApplicationContext();
        mVolleyService = new Volley(mContext);

        SharedPreferences token = getActivity().getSharedPreferences(AppConfig.PREF_APIKEY, MODE_PRIVATE);
        apikey = token.getString("apikey", "");

        searchView = ((SearchView) rootView.findViewById(R.id.user_searchView));

        lv = (ListView)rootView.findViewById(R.id.lv_petugas);
        lv.setTextFilterEnabled(true);

        setupSearchView();
        FloatingActionButton btn_adduser = ((FloatingActionButton) rootView.findViewById(R.id.fab_add_user));
        btn_adduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddUserActivity.class);
                startActivityForResult(intent, 10001);
            }
        });

        //getAllUserRumahPompa();
        getAllUserRumahPompa(new ServerCallback() {
            @Override
            public void onSuccess(Map<String, String> list) {
                // call web service get all user

                final Map<String, String> lists = list;
                mVolleyService.getAll(AppConfig.URL_USER, apikey, new VolleyResponseListener() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray result = response.getJSONArray("result");

                            for (int i=0; i<result.length(); i++){
                                JSONObject re = result.getJSONObject(i);
                                final String username = re.getString("username");
                                String nama_user = re.getString("nama_user");
                                String nohp = re.getString("no_telp_user");

                                final User petugas = new User();
                                petugas.setUsername(username);
                                petugas.setNama(nama_user);
                                petugas.setNohp(nohp);
                                petugas.setRumahpompa(lists.get(username));
                                userlist.add(petugas);
                            }
                            Collections.sort(userlist, new Comparator<User>() {
                                @Override
                                public int compare(User a1, User a2) {
                                    return (a1.nama.toString()).compareToIgnoreCase(a2.nama.toString());
                                }
                            });
                            lv.setAdapter(new UserAdapter(getActivity(), userlist));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView rumahpompa = (TextView)view.findViewById(R.id.tv_rmhpompapetugas);
                TextView username = (TextView)view.findViewById(R.id.tv_usernamepetugas);
                String nama_rumahpompa = rumahpompa.getText().toString();
                String user = username.getText().toString();
                Intent intent = new Intent(getActivity().getApplicationContext(), UserDetailActivity.class);
                intent.putExtra("username", user);
                intent.putExtra("rumahpompa", nama_rumahpompa);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void getAllUserRumahPompa(final ServerCallback callback){
        mVolleyService.getAll(AppConfig.URL_GETUSERRUMAHPOMPA, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray result = response.getJSONArray("result");

                    for (int i=0; i<result.length(); i++){
                        JSONObject re = result.getJSONObject(i);
                        String username = re.getString("username");
                        String nama_rumahpompa = re.getString("rumahpompa");
                        user_rumahpompa.put(username, nama_rumahpompa);

                    }
                    callback.onSuccess(user_rumahpompa);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

   /* private void getAllUserRumahPompa(final ServerCallback callback){
        String tag_string_req = "req_getalluserrumahpompa";
        //showProgress(true);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GETUSERRUMAHPOMPA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Toast.makeText(getActivity().getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                //showProgress(false);
                try {
                    //JSONObject jObj = new JSONObject(response);
                    JSONArray result = new JSONArray(response);

                    for (int i=0; i<result.length(); i++){
                        JSONObject re = result.getJSONObject(i);
                        String username = re.getString("username");
                        String nama_rumahpompa = re.getString("rumahpompa");
                        user_rumahpompa.put(username, nama_rumahpompa);

                    }
                    callback.onSuccess(user_rumahpompa);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10001) && (resultCode == Activity.RESULT_OK)){
            // recreate your fragment here
            DataUserFragment frg = new DataUserFragment();
                    //(DataUserFragment) getFragmentManager().findFragmentById(R.id.fragment_datauser);
            getFragmentManager().beginTransaction()
                    .detach(frg)
                    .attach(frg)
                    .commit();
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public interface ServerCallback {
        void onSuccess(Map<String, String> list);
    }

    private void setupSearchView()
    {
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Masukkan Nama User");
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //UserAdapter adapter = (UserAdapter) lv.getAdapter();
        if (TextUtils.isEmpty(newText)) {
            lv.clearTextFilter();
            //adapter.getFilter().filter(null);
        } else {
            lv.setFilterText(newText);
            //adapter.getFilter().filter(newText);
        }
        return true;
    }

    private void updateList(ArrayList<User> mList) {
        this.userlist.clear();
        this.userlist.addAll(mList);
    }

    /*@Override
    public void onResume() {
        // fetch updated data
        UserAdapter.updateList(mChatDetails);
        UserAdapter.notifyDataSetChanged();
    }*/

}
