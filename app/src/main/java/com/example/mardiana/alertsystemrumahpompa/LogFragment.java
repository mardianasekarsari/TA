package com.example.mardiana.alertsystemrumahpompa;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText edt_log;
    Context mContext;
    Volley mVolleyService;
    private ListView lv_log;
    private String apikey, username, idrumahpompa;
    private SessionManager session;

    private OnFragmentInteractionListener mListener;

    public LogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogFragment newInstance(String param1, String param2) {
        LogFragment fragment = new LogFragment();
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
        View view = inflater.inflate(R.layout.fragment_log, container, false);

        session = new SessionManager(getActivity().getApplicationContext());

        HashMap<String, String> user = session.getUser();

        username = user.get(SessionManager.KEY_USERNAME);
        idrumahpompa = user.get(SessionManager.KEY_RUMAHPOMPAID);

        mContext = getActivity().getApplicationContext();
        mVolleyService = new Volley(mContext);

        SharedPreferences token = getActivity().getSharedPreferences(AppConfig.PREF_APIKEY, MODE_PRIVATE);
        apikey = token.getString("apikey", "");

        edt_log = ((EditText) view.findViewById(R.id.edt_log));
        Button btn_log = ((Button) view.findViewById(R.id.btn_log));

        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemSubmit();
            }
        });

        return view;
    }

    private void attemSubmit() {
        edt_log.setError(null);

        String log = edt_log.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(log)) {
            edt_log.setError(getString(R.string.error_field_required));
            focusView = edt_log;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            storeLog(username, idrumahpompa, log);
        }
    }

    private void storeLog(final String username, final String id_rumah_pompa, final String log){
        mVolleyService.addLog(username, id_rumah_pompa, log, apikey, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public ArrayList<String> onResponse(JSONObject response) {
                try {
                    boolean status = response.getBoolean("status");

                    if (status) {
                        String msg = response.getString("msg");
                        Toast.makeText(mContext, AppConfig.STORE_SUCCESS, Toast.LENGTH_SHORT).show();
                        edt_log.setText("");
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = response.getString("msg");
                        String kode = response.getString("kode");
                        if (kode.equals("1")){
                            Toast.makeText(mContext, AppConfig.STORE_FAILED, Toast.LENGTH_SHORT).show();
                        }else if (kode.equals("0")){
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
}
