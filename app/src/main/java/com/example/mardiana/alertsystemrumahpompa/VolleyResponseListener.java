package com.example.mardiana.alertsystemrumahpompa;

import org.json.JSONObject;

/**
 * Created by Mardiana on 4/29/2017.
 */

public interface VolleyResponseListener {
    public void onResponse(JSONObject response);

    public void onError( String message);
}
