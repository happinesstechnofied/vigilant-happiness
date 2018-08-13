package com.vendorprovider;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.connection.ConnectionDetector;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kprogresshud.KProgressHUD;
import model.QueAnsHistory;
import model.ServicesData;
import services.Services;
import util.AppConstants;
import util.SharedPrefManager;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageView imgPostAd;
    /* flag for Internet connection status*/
    Boolean isInternetPresent = false;
    /*Connection detector class*/
    ConnectionDetector cd;
    private KProgressHUD hud;
    private static final int READ_STORAGE_CODE = 1001;
    SharedPreferences preferences;
    RequestQueue requestQueue;
    public static JSONArray finalData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.main_activity);
        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        initView();

        getSupportActionBar().setTitle("Home");

        imgPostAd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("EditService", "Add");
                    editor.commit();
                    startActivity(new Intent(MainActivity.this, ServiceCreationActivity.class));
                } else {
                    requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, READ_STORAGE_CODE);
                }
            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                selectedFragment = HomeFragment.newInstance();
                                getSupportActionBar().setTitle("Home");
                                imgPostAd.setVisibility(View.VISIBLE);
                                break;
                            case R.id.action_reply:
                                selectedFragment = ReplyFragment.newInstance();
                                getSupportActionBar().setTitle("Reply");
                                imgPostAd.setVisibility(View.GONE);
                                break;
                            case R.id.action_chat:
                                selectedFragment = ChatFragment.newInstance();
                                getSupportActionBar().setTitle("Chat");
                                imgPostAd.setVisibility(View.GONE);
                                break;
                            case R.id.action_profile:
                                selectedFragment = ProfileFragment.newInstance();
                                getSupportActionBar().setTitle("Profile");
                                imgPostAd.setVisibility(View.GONE);
                                break;
                            case R.id.action_more:
                                selectedFragment = MoreFragment.newInstance();
                                getSupportActionBar().setTitle("More");
                                imgPostAd.setVisibility(View.GONE);
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });
        cd = new ConnectionDetector(MainActivity.this);
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            hud = KProgressHUD.create(MainActivity.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait");
            hud.show();
            GetServiceData();
        } else {
            Toast.makeText(MainActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }


    }

    private void initView() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        imgPostAd = (ImageView) findViewById(R.id.imgPostAd);
    }

    private void GetServiceData() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.GET_SERVICE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (hud.isShowing())
                            hud.dismiss();

                        try {

                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("services");
                            TypeReference<ArrayList<ServicesData>> typeRef = new TypeReference<ArrayList<ServicesData>>() {
                            };

                            finalData = jsonArray;

                            //Manually displaying the first fragment - one time only
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frame_layout, HomeFragment.newInstance());
                            transaction.commit();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        if (hud.isShowing())
                            hud.dismiss();
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Service", "get_sp_service_list");
                if (preferences.getString("user_data", "") != null && !preferences.getString("user_data", "").equalsIgnoreCase("")) {
                    try {
                        JSONObject jObj = new JSONObject(preferences.getString("user_data", ""));
                        params.put("user_id", jObj.getString("user_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(MainActivity.this);
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }


    private boolean isPermissionGranted(String permission) {
        int result = ContextCompat.checkSelfPermission(this, permission);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }

    //Requesting permission
    private void requestPermission(String permission, int code) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
        }
        ActivityCompat.requestPermissions(this, new String[]{permission}, code);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        menu.findItem(R.id.action_more).setVisible(true);
        menu.findItem(R.id.action_ascending).setVisible(true);
        menu.findItem(R.id.action_descending).setVisible(true);
        menu.findItem(R.id.action_time_asc).setVisible(true);
        menu.findItem(R.id.action_time_des).setVisible(true);
        menu.findItem(R.id.action_logout).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_ascending:
                return true;
            case R.id.action_descending:
                return true;
            case R.id.action_time_asc:
                return true;
            case R.id.action_time_des:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
