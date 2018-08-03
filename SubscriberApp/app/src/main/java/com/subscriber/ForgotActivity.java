package com.subscriber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import connection.ConnectionDetector;
import kprogresshud.KProgressHUD;
import services.Services;

public class ForgotActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private KProgressHUD hud;
    private Button btnSendMail;
    TextInputEditText txtForgotMail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_activity);
        initView();

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Forgot Password");

        /*creating connection detector class instance*/
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        btnSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (TextUtils.isEmpty(txtForgotMail.getText().toString())) {
                    txtForgotMail.setError("Please enter your registered mobile no");
                    txtForgotMail.requestFocus();
                    return;
                } else if (!isValidMobile(txtForgotMail.getText().toString())) {
                    txtForgotMail.setError("Mobile no is not valid!");
                    txtForgotMail.requestFocus();
                } else {
                    if (isInternetPresent) {
                        sendMail();
                    } else {
                        Toast.makeText(ForgotActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void initView() {
        txtForgotMail = (TextInputEditText) findViewById(R.id.txtForgotMail);
        btnSendMail = (Button) findViewById(R.id.btnSendMail);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendMail() {
        hud = KProgressHUD.create(ForgotActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Sending OTP...");
        hud.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.FORGOT_PASS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jObj = new JSONObject(response.toString());
                            String status = jObj.getString("status");
                            if (status.matches("success")) {

                                String USER_ID = jObj.getString("user_id");
                                String pincode = jObj.getString("pincode");

                                if (hud.isShowing())
                                    hud.dismiss();

                                Intent intent = new Intent(ForgotActivity.this, OTPVerifyActivity.class);
                                intent.putExtra("intent_from", "forgot_pass");
                                intent.putExtra("user_id", USER_ID);
                                intent.putExtra("pincode", pincode);
                                startActivity(intent);
                                finish();


                            } else {
                                if (hud.isShowing())
                                    hud.dismiss();

                                String failed = jObj.getString("message");
                                Toast.makeText(ForgotActivity.this, failed, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(ForgotActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Service", "send_otp");
                params.put("mobile_no", txtForgotMail.getText().toString());
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(ForgotActivity.this);
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }

    boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }
}