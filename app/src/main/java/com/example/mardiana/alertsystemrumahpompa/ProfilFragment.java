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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfilFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfilFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static String rumahpompa="";

    private LinearLayout ll_logout;
    private LinearLayout ll_rumahpompa;
    private SessionManager session;
    private LoginSQLiteHandler db;
    private String apikey;

    private TextView tv_petugas_usernama, tv_petugas_nama, tv_petugas_nohp, tv_petugas_alamat, tv_petugas_rumahpompa;
    private FloatingActionButton btn_edit;

    Context mContext;
    Volley mVolleyService;

    private OnFragmentInteractionListener mListener;

    public ProfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfilFragment newInstance(String param1, String param2) {
        ProfilFragment fragment = new ProfilFragment();
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
        //db = new LoginSQLiteHandler(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        db = new LoginSQLiteHandler(getActivity());
        session = new SessionManager(getActivity().getApplicationContext());

        mContext = getActivity().getApplicationContext();
        mVolleyService = new Volley(mContext);

        SharedPreferences token = getActivity().getSharedPreferences(AppConfig.PREF_APIKEY, MODE_PRIVATE);
        apikey = token.getString("apikey", "");
        /*Toast.makeText(mContext, apikey, Toast.LENGTH_SHORT).show();*/

        tv_petugas_usernama = ((TextView) view.findViewById(R.id.tv_petugas_username));
        tv_petugas_nama = ((TextView) view.findViewById(R.id.tv_petugas_nama));
        tv_petugas_nohp = ((TextView) view.findViewById(R.id.tv_petugas_nohp));
        tv_petugas_alamat = ((TextView) view.findViewById(R.id.tv_petugas_alamat));
        tv_petugas_rumahpompa = ((TextView) view.findViewById(R.id.tv_petugas_rumahpompa));
        ll_rumahpompa = ((LinearLayout) view.findViewById(R.id.ll_rumahpompa));


        //HashMap<String, String> user = db.getUserDetails();
        HashMap<String, String> user = session.getUser();

        final String username = user.get(SessionManager.KEY_USERNAME);
        String nama = user.get(SessionManager.KEY_NAME);
        String nohp = user.get(SessionManager.KEY_PHONE);
        String alamat = user.get(SessionManager.KEY_ADDRESS);
        String idrumahpompa = user.get(SessionManager.KEY_RUMAHPOMPAID);
        String role = user.get(SessionManager.KEY_ROLE);

        /*final String username = user.get("username");
        String nama = user.get("nama");
        String nohp = user.get("telepon_user");
        String alamat = user.get("alamat_user");
        String idrumahpompa = user.get("rumahpompa");*/

        if (role.equals(AppConfig.ADMIN) || role.equals(AppConfig.PENGAWAS)){
            ll_rumahpompa.setVisibility(View.GONE);
        }
        else if (role.equals(AppConfig.PETUGAS)){
            getrumahpompabyId(idrumahpompa);
        }

        //alert(idrumahpompa);

        tv_petugas_usernama.setText(username);
        tv_petugas_nama.setText(nama);
        tv_petugas_nohp.setText(nohp);
        tv_petugas_alamat.setText(alamat);

        //Toast.makeText(getActivity(), username + " " + nama + " " + nohp + " " + alamat, Toast.LENGTH_SHORT).show();

        /*Button btn = ((Button) view.findViewById(R.id.btn_lala));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Lala", Toast.LENGTH_SHORT).show();
            }
        });*/
        ll_logout = ((LinearLayout) view.findViewById(R.id.ll_petugas_logout));
        ll_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete(username);
            }
        });

        //Floating Action Button Edit Clicked
        btn_edit = ((FloatingActionButton) view.findViewById(R.id.fab_petugas_edit));
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfilActivity.class);
                getActivity().startActivity(intent);
            }
        });

        LinearLayout ll_changepassword = ((LinearLayout) view.findViewById(R.id.ll_petugas_ubahpassword));
        ll_changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword(username);
            }
        });

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
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void getrumahpompabyId(final String id) {
        String url = AppConfig.URL_RUMAHPOMPA + id;
        mVolleyService.getBy(url, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    rumahpompa = response.getString("nama_");

                    tv_petugas_rumahpompa.setText(rumahpompa);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                //Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();

                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject result = jObj.getJSONObject("result");

                    rumahpompa = result.getString("nama_");

                    tv_petugas_rumahpompa.setText(rumahpompa);
                    //Toast.makeText(getActivity().getApplicationContext(), rumahpompa, Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Json4 error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
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

    private void delete(String username){
        //String post_data = Uri.encode()
        String url = AppConfig.URL_USER + username + "/token";

        //String url = AppConfig.URL_USER + "deleteToken/" + username;
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
                        session.logoutUser();
                        db.deleteUsers();
                        getActivity().finish();
                    } else {
                        String errorMsg = response.getString("msg");
                        Toast.makeText(mContext, "Logout Gagal", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changePassword(String username){
        Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
        intent.putExtra("username", username);
        startActivityForResult(intent, 10003);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10003) && (resultCode == Activity.RESULT_OK)){
            // recreate your fragment here
            ProfilFragment frg = new ProfilFragment();
            //(DataUserFragment) getFragmentManager().findFragmentById(R.id.fragment_datauser);
            getFragmentManager().beginTransaction()
                    .detach(frg)
                    .attach(frg)
                    .commit();
        }
    }

    /*private void alert(final String id) {
        String tag_string_req = "req_alert";
        //showProgress(true);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ALERT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //showProgress(false);
                //Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                    if (status) {
                        Toast.makeText(getActivity().getApplicationContext(), "Ada Alert", Toast.LENGTH_SHORT).show();

                    } else {
                        //Error in login. Get the error message
                        Toast.makeText(getActivity().getApplicationContext(), "Tidak Ada Alert", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Login Error: " + error.getMessage());
                //Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
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
}


