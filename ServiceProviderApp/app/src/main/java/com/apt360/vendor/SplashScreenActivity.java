package com.apt360.vendor;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.connection.ConnectionDetector;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import services.Services;


/**
 * Created by Piyush on 10/16/2017.
 */

public class SplashScreenActivity extends AppCompatActivity {

    /* flag for Internet connection status*/
    Boolean isInternetPresent = false;
    /*Connection detector class*/
    ConnectionDetector cd;
    RequestQueue requestQueue;
    private static final int PERMISSION_REQUEST_CODE = 200;
    int READ_PHONE_STATE_ = 10;
    SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.splash_screen);

        //Log.d("Device_id", FirebaseInstanceId.getInstance().getToken());

        preferences = PreferenceManager.getDefaultSharedPreferences(SplashScreenActivity.this);

        /*creating connection detector class instance*/
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();


        // check internet connection.....
        if (isInternetPresent) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Notification();
                }
            }, 1000);
        } else {
            AlertDialog.Builder alertDialogCancel = new AlertDialog.Builder(SplashScreenActivity.this);
            alertDialogCancel.setTitle("No Internet Connection!");
            alertDialogCancel.setIcon(R.drawable.ic_internet);// Set Alert dialog title here
            alertDialogCancel.setMessage("You don't have internet connection,Please Check your Internet Connection");
            alertDialogCancel.setPositiveButton("Setting",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    });
            alertDialogCancel.setNegativeButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
            AlertDialog cancelFailedAlertDialog = alertDialogCancel.create();
            cancelFailedAlertDialog.setCancelable(false);
            cancelFailedAlertDialog.show();
        }
    }

    private void SaveDeviceId() {

        if (preferences.getString("user_data", "") != null && !preferences.getString("user_data", "").equalsIgnoreCase("")) {
            try {
                JSONObject jobj = new JSONObject(preferences.getString("user_data", ""));
                if (!jobj.getString("user_id").equalsIgnoreCase("")) {
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                    //GetServiceData();
                } else {
                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        } else {

            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void Notification() {
        TelephonyManager tm = (TelephonyManager) this.getSystemService(SplashScreenActivity.this.TELEPHONY_SERVICE);
        final String countryCodeValue = tm.getNetworkCountryIso();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.NOTIFICATION_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SaveDeviceId();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        SaveDeviceId();
                        // error
                        Log.d("Error.Response", error.toString());
                        //Toast.makeText(SplashScreenActivity.this, "N", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Service", "submit_device_token");
                params.put("device_id", FirebaseInstanceId.getInstance().getToken());
                params.put("device_type", "1");
                params.put("country", countryCodeValue.toUpperCase());
                params.put("user_type", "1");
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(SplashScreenActivity.this);
        requestQueue.add(postRequest);
    }

//        TelephonyManager tm = (TelephonyManager)this.getSystemService(SplashScreenActivity.this.TELEPHONY_SERVICE);
//        final String countryCodeValue = tm.getNetworkCountryIso();
//
//        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.FILTER_URL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // error
//                        Log.d("Error.Response", error.toString());
//                        Toast.makeText(SplashScreenActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Service", "submit_device_token");
//                //params.put("deviceid", FirebaseInstanceId.getInstance().getToken());
//                params.put("deviceid", "1234567890");
//                params.put("devicetype", "1");
//                return params;
//            }
//        };
//        requestQueue = Volley.newRequestQueue(SplashScreenActivity.this);
//        requestQueue.add(postRequest);


    private void GetServiceData() {
//        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.GET_SERVICE_URL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject obj = new JSONObject(response);
//                            JSONArray jsonArray = obj.getJSONArray("services");
//                            TypeReference<ArrayList<ServicesData>> typeRef = new TypeReference<ArrayList<ServicesData>>() {
//                            };
//
//                            finalData = jsonArray;
//
//                            arrayList = AppConstants.getMapper().readValue(jsonArray.toString(), typeRef);
//                            adapter = new HomeFragmentAdapter(getContext(),arrayList);
//                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
//                            recyclerView.setLayoutManager(mLayoutManager);
//                            recyclerView.setItemAnimator(new DefaultItemAnimator());
//                            recyclerView.setAdapter(adapter);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        } catch (JsonParseException e) {
//                            e.printStackTrace();
//                        } catch (JsonMappingException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // error
//                        Log.d("Error.Response", error.toString());
//                        //Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Service", "get_sp_service_list");
//                if (preferences.getString("user_data", "") != null && !preferences.getString("user_data", "").equalsIgnoreCase("")) {
//                    try {
//                        JSONObject jObj = new JSONObject(preferences.getString("user_data", ""));
//                        params.put("user_id", jObj.getString("user_id"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Intent intent = new Intent(getContext(), LoginActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                }
//                return params;
//            }
//        };
//        requestQueue = Volley.newRequestQueue(SplashScreenActivity.this);
//        postRequest.setShouldCache(false);
//        requestQueue.add(postRequest);
    }

}
