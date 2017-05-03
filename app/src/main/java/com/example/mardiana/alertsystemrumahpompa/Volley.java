package com.example.mardiana.alertsystemrumahpompa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mardiana on 4/29/2017.
 */

public class Volley {

    Context mContext;

    Volley(Context context){
        mContext = context;
    }

    public void getBy(final String url, final String apikey, final VolleyResponseListener volleyResponseListener){
        final String tag_string_req = "req_getuserbyusername";
        //showProgress(true);
        //String url = AppConfig.URL_USER + username;

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Toast.makeText(getActivity().getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                //showProgress(false);
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject result = jObj.getJSONObject("result");

                    volleyResponseListener.onResponse(result);

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
                volleyResponseListener.onError(error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Api-key", apikey);
                return headers;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void getAll(final String url, final String apikey, final VolleyResponseListener volleyResponseListener){
        String tag_string_req = "req_getall";
        //showProgress(true);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Toast.makeText(getActivity().getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                //showProgress(false);
                try {
                    JSONObject jObj = new JSONObject(response);
                    volleyResponseListener.onResponse(jObj);

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
                volleyResponseListener.onError(error.toString());
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void register(final String username, final String rumah_pompa, final String name, final String role, final String address, final String phone, final String password, final String apikey, final VolleyResponseListener volleyResponseListener) {
        String tag_string_req = "req_register";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_USER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    volleyResponseListener.onResponse(jObj);
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Login Error: " + error.getMessage());
                volleyResponseListener.onError(error.toString());
                //showProgress(false);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("name", name);
                params.put("address", address);
                params.put("phone", phone);
                params.put("password", password);
                params.put("role", role);
                params.put("rumah_pompa", rumah_pompa);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void editUser(final String username, final String rumah_pompa, final String name, final String role, final String address, final String phone, final String apikey, final VolleyResponseListener volleyResponseListener) {
        String tag_string_req = "req_edituser";
        StringRequest strReq = new StringRequest(Request.Method.PUT,
                AppConfig.URL_USER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jObj = new JSONObject(response);
                    volleyResponseListener.onResponse(jObj);

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Login Error: " + error.getMessage());
                volleyResponseListener.onError(error.toString());
                //showProgress(false);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("name", name);
                params.put("address", address);
                params.put("phone", phone);
                params.put("role", role);
                params.put("rumah_pompa", rumah_pompa);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void delete(final String url, final String apikey, final VolleyResponseListener volleyResponseListener) {
        String tag_string_req = "req_delete";

        StringRequest strReq = new StringRequest(Request.Method.DELETE,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    volleyResponseListener.onResponse(jObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "GetData Error: " + error.getMessage());
                volleyResponseListener.onError(error.toString());
                //showProgress(false);
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void editToken(final String username, final String token, final String apikey, final VolleyResponseListener volleyResponseListener) {
        String tag_string_req = "req_editToken";
        //showProgress(true);
        String url = AppConfig.URL_USER + username + "/token";
        StringRequest strReq = new StringRequest(Request.Method.PUT,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //showProgress(false);
                //Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jObj = new JSONObject(response);
                    volleyResponseListener.onResponse(jObj);

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Login Error: " + error.getMessage());
                volleyResponseListener.onError(error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void changePassword(final String username, final String oldpassword, final String newpassword, final String apikey, final VolleyResponseListener volleyResponseListener) {
        String tag_string_req = "req_changepassword";
        String url = AppConfig.URL_USER + username + "/password";
        //showProgress(true);

        StringRequest strReq = new StringRequest(Request.Method.PUT,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //showProgress(false);
                //Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jObj = new JSONObject(response);
                    volleyResponseListener.onResponse(jObj);
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Login Error: " + error.getMessage());
                volleyResponseListener.onError(error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("oldpassword", oldpassword);
                params.put("newpassword", newpassword);

                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void addRumahPompa(final String name, final String phone, final String address, final String threshold,
                       final String depth, final String latitude, final String longitude,
                              final String apikey, final VolleyResponseListener volleyResponseListener) {
        String tag_string_req = "req_store_rumahpompa";
        //showProgress(true);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_RUMAHPOMPA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //showProgress(false);
                try {
                    JSONObject jObj = new JSONObject(response);
                    volleyResponseListener.onResponse(jObj);
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                volleyResponseListener.onError(error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("phone", phone);
                params.put("address", address);
                params.put("threshold", threshold);
                params.put("latitude", latitude);
                params.put("longitude", longitude);
                params.put("depthofriver", depth);

                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void editRumahPompa(final String id, final String name, final String phone, final String address,
                                final String threshold, final String latitude, final String longitude,
                                final String kedalamansaluran, final String apikey, final VolleyResponseListener volleyResponseListener) {
        String tag_string_req = "req_edit_rumahpompa";
        //showProgress(true);

        StringRequest strReq = new StringRequest(Request.Method.PUT,
                AppConfig.URL_RUMAHPOMPA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //showProgress(false);
                //Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jObj = new JSONObject(response);
                    volleyResponseListener.onResponse(jObj);
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Login Error: " + error.getMessage());
                volleyResponseListener.onError(error.toString());
                //showProgress(false);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("name", name);
                params.put("phone", phone);
                params.put("address", address);
                params.put("threshold", threshold);
                params.put("latitude", latitude);
                params.put("longitude", longitude);
                params.put("depthofinlet", kedalamansaluran);

                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void getrumahpompabyName(final String name, final String apikey, final VolleyResponseListener volleyResponseListener) {
        String tag_string_req = "req_getrumahpompabyName";
        String url = AppConfig.URL_RUMAHPOMPA + "getrumahpompabyName/" + name ;
        //showProgress(true);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETRUMAHPOMPABYNAME, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //showProgress(false);
                //Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();

                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject result = jObj.getJSONObject("result");
                    volleyResponseListener.onResponse(result);

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                volleyResponseListener.onError(error.toString());
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);

                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void showbyStatus(final String status, final String apikey, final VolleyResponseListener volleyResponseListener){
        String tag_string_req = "req_getrumahpompabystatus";
        //String url = AppConfig.URL_RUMAHPOMPA + status + "/status";
        //String url = "http://192.168.1.101/rumahpompa-server/rumah-pompa/status/"+status;
        //showProgress(true);
        //Toast.makeText(mContext, AppConfig.URL_RUMAHPOMPA+"?status="+status, Toast.LENGTH_SHORT).show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETRUMAHPOMPABYSTATUS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //
                //showProgress(false);
                try {
                    //
                    JSONObject result = new JSONObject(response);
                    volleyResponseListener.onResponse(result);

                } catch (JSONException e) {
                    //JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "GetData Error: " + error.getMessage());
                volleyResponseListener.onError(error.toString());

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
    }

    public void getApiKey(final String key, final String password, final String username, final VolleyResponseListener volleyResponseListener){
        String tag_string_req = "req_getapikey";
        //String url = AppConfig.URL_RUMAHPOMPA + status + "/status";
        //String url = "http://192.168.1.101/rumahpompa-server/rumah-pompa/status/"+status;
        //showProgress(true);
        //Toast.makeText(mContext, AppConfig.URL_RUMAHPOMPA+"?status="+status, Toast.LENGTH_SHORT).show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_APIKEY, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //
                //showProgress(false);
                try {
                    //
                    JSONObject result = new JSONObject(response);
                    JSONObject result2 = result.getJSONObject("result");
                    volleyResponseListener.onResponse(result2);

                } catch (JSONException e) {
                    //JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "GetData Error: " + error.getMessage());
                volleyResponseListener.onError(error.toString());

                //showProgress(false);
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("key", key);
                params.put("password", password);
                params.put("username", username);

                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
