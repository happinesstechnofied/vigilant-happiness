package com.vendorprovider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.connection.ConnectionDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kprogresshud.KProgressHUD;
import services.Services;

/**
 * Created by Piyush on 10/31/2017.
 */

public class SignUpDataActivity extends AppCompatActivity {

    TextInputEditText txtContact, txtEmail ,txtName;
    Button btnCreatePasscode;
    String newPassword, newConfirmPassword;
    String userEmail;
    /* flag for Internet connection status*/
    Boolean isInternetPresent = false;
    /*Connection detector class*/
    ConnectionDetector cd;
    private KProgressHUD hud;
    RequestQueue requestQueue;
    String user_id = "";
    SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_data_activity);
        preferences = PreferenceManager.getDefaultSharedPreferences(SignUpDataActivity.this);
        /*creating connection detector class instance*/
        cd = new ConnectionDetector(SignUpDataActivity.this);
        isInternetPresent = cd.isConnectingToInternet();

        //Intent intent = getIntent();
        //user_id = intent.getStringExtra("user_id");

        initView();

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create New Password");

        btnCreatePasscode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (isInternetPresent) {
                    // Check input value...
                    if (TextUtils.isEmpty(txtContact.getText().toString())) {
                        txtContact.setError("Please enter your mobile no");
                        txtContact.requestFocus();
                        return;
                    }
                    if (TextUtils.isEmpty(txtEmail.getText().toString())) {
                        txtEmail.setError("Please enter your email");
                        txtEmail.requestFocus();
                        return;
                    }
                    // check confirm password match or not
                    if (TextUtils.isEmpty(txtName.getText().toString())) {
                        txtName.setError("Please enter your name");
                        txtName.requestFocus();
                        return;
                    } else {
                        CreateNewPasscode();
                        hud = KProgressHUD.create(SignUpDataActivity.this)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setLabel("Please wait");
                        hud.show();
                    }
                } else {
                    Toast.makeText(SignUpDataActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void CreateNewPasscode() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.SIGN_SOCIAL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response.toString());
                            String status = jObj.getString("status");
                            if (status.matches("success")) {

                                JSONObject innerjObj = new JSONObject(jObj.getString("user_data"));
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("USER_ID", innerjObj.getString("user_id"));
                                editor.putString("login", "Manually");
                                editor.putString("user_data", jObj.getString("user_data"));
                                editor.commit();

                                if (hud.isShowing())
                                    hud.dismiss();

                                editor.putString("EditService", "Add");
                                editor.commit();
                                Intent intent = new Intent(SignUpDataActivity.this, ServiceCreationActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            } else {
                                if (hud.isShowing())
                                    hud.dismiss();

                                String failed = jObj.getString("message");
                                Toast.makeText(SignUpDataActivity.this, failed, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignUpDataActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Service", "sign_up_social");
                params.put("mobile_no", txtContact.getText().toString());
                params.put("email", txtEmail.getText().toString());
                params.put("user_name", txtName.getText().toString());
                params.put("device_id", "124567890");
                params.put("device_type", "1");
//                params.put("profile_image", );
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(SignUpDataActivity.this);
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }

    private void initView() {

        txtContact = (TextInputEditText) findViewById(R.id.txtContact);
        txtEmail = (TextInputEditText) findViewById(R.id.txtEmail);
        txtName = (TextInputEditText) findViewById(R.id.txtUserName);
        btnCreatePasscode = (Button) findViewById(R.id.btnCreatePasscode);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}
