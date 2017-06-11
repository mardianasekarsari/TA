package com.example.mardiana.alertsystemrumahpompa;

import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mardiana on 4/29/2017.
 */

public interface VolleyResponseListener {
    public ArrayList<String> onResponse(JSONObject response);

    public void onError( String message);
}
