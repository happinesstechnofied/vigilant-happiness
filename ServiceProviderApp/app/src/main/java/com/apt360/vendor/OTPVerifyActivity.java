package com.apt360.vendor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
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
import com.connection.ConnectionDetector;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.raycoarana.codeinputview.CodeInputView;
import com.raycoarana.codeinputview.OnCodeCompleteListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import services.Services;

public class OTPVerifyActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private KProgressHUD hud;
    private Button btnVerifyNow, btnResendNow;
    CodeInputView txtCodeInput;
    String getOTPCode;
    private Handler mHandler = new Handler();
    String user_id = "", mobile_no = "";
    String sended_OTP = "";
    String intent_from = "";
    SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_activity);

        preferences = PreferenceManager.getDefaultSharedPreferences(OTPVerifyActivity.this);

        final Intent intent = getIntent();
        if (intent.getStringExtra("intent_from") != null && intent.getStringExtra("intent_from").equalsIgnoreCase("registration")) {
            intent_from = intent.getStringExtra("intent_from");
            user_id = intent.getStringExtra("user_id");
            sended_OTP = intent.getStringExtra("pincode");
        }
        if (intent.getStringExtra("intent_from") != null && intent.getStringExtra("intent_from").equalsIgnoreCase("forgot_pass")) {
            intent_from = intent.getStringExtra("intent_from");
            user_id = intent.getStringExtra("user_id");
            sended_OTP = intent.getStringExtra("pincode");

        }
        initView();

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Verification");

        /*creating connection detector class instance*/
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        btnVerifyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtCodeInput.setEditable(true);
                getOTPCode = txtCodeInput.getCode();

                if (sended_OTP.equals(getOTPCode)) {
//                    Toast.makeText(OTPVerifyActivity.this, "Successfully Match!", Toast.LENGTH_SHORT).show();

                    if (intent.getStringExtra("intent_from") != null && intent.getStringExtra("intent_from").equalsIgnoreCase("registration")) {
                        hud = KProgressHUD.create(OTPVerifyActivity.this)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setLabel("Verifying OTP...");
                        hud.show();
                        createUser();
                    } else {

                        Intent intent = new Intent(OTPVerifyActivity.this, ChangeForgotActivity.class);
                        intent.putExtra("user_id", user_id);
                        startActivity(intent);
                    }

                } else if (getOTPCode.equals("")) {
                    txtCodeInput.setError("Please Enter OTP");
                } else {
                    txtCodeInput.setError("Your code is incorrect");
                }
            }
        });

        btnResendNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hud = KProgressHUD.create(OTPVerifyActivity.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("Sending OTP...");
                hud.show();
                if (intent_from.equalsIgnoreCase("registration")) {
                    resendOTPregistration();
                }
                if (intent_from.equalsIgnoreCase("forgot_pass")) {
                    resendOTPforgot();
                }
            }
        });

        txtCodeInput.addOnCompleteListener(new OnCodeCompleteListener() {
            @Override
            public void onCompleted(String code) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Make the input enable again so the user can change it
                        txtCodeInput.setEditable(true);
                    }
                }, 1000);
            }
        });
    }

    private void initView() {
        btnVerifyNow = (Button) findViewById(R.id.btnVerifyNow);
        btnResendNow = (Button) findViewById(R.id.btnResendNow);
        txtCodeInput = (CodeInputView) findViewById(R.id.txtCodeInput);
        txtCodeInput.setEditable(true);
        /*txtCodeInput.setInPasswordMode(true);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void resendOTPregistration() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.RESEND_OTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jObj = new JSONObject(response.toString());
                            String status = jObj.getString("status");
                            if (status.matches("success")) {

                                sended_OTP = jObj.getString("pincode");
                                //starting the profile activity
//                                Toast.makeText(RegisterActivity.this,"Successful registration!",Toast.LENGTH_SHORT).show();
//                                finish();
                                if (hud.isShowing())
                                    hud.dismiss();

                            } else {
                                if (hud.isShowing())
                                    hud.dismiss();

                                String failed = jObj.getString("message");
                                Toast.makeText(OTPVerifyActivity.this, failed, Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(OTPVerifyActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Service", "resend_otp");
                params.put("user_id", user_id);
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(OTPVerifyActivity.this);
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }


    public void resendOTPforgot() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.FORGOT_RESEND_OTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jObj = new JSONObject(response.toString());
                            String status = jObj.getString("status");
                            if (status.matches("success")) {

                                sended_OTP = jObj.getString("pincode");
                                if (hud.isShowing())
                                    hud.dismiss();

                            } else {
                                if (hud.isShowing())
                                    hud.dismiss();

                                String failed = jObj.getString("message");
                                Toast.makeText(OTPVerifyActivity.this, failed, Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(OTPVerifyActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Service", "resend_otp");
                params.put("user_id", user_id);
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(OTPVerifyActivity.this);
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }

    public void createUser() {
        TelephonyManager tm = (TelephonyManager) this.getSystemService(OTPVerifyActivity.this.TELEPHONY_SERVICE);
        final String countryCodeValue = tm.getNetworkCountryIso();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.CREATE_USER_URL,
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
                                SendNotify(innerjObj.getString("user_name"));
//                                Intent intent = new Intent(OTPVerifyActivity.this, ServiceCreationActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);
                                Intent intent = new Intent(OTPVerifyActivity.this, TutorialActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            } else {
                                if (hud.isShowing())
                                    hud.dismiss();

                                String failed = jObj.getString("message");
                                Toast.makeText(OTPVerifyActivity.this, failed, Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(OTPVerifyActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Service", "create_user_sp");
                params.put("user_id", user_id);
                params.put("user_type", "1");
                params.put("country", countryCodeValue.toUpperCase());
                params.put("device_id", FirebaseInstanceId.getInstance().getToken());
                params.put("device_type", "1");
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(OTPVerifyActivity.this);
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }

    private void SendNotify(String user_name) {
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        android.support.v4.app.NotificationCompat.Builder notificationBuilder =
                new android.support.v4.app.NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getBaseContext().getResources(), R.mipmap.ic_launcher))
                        .setContentTitle("APT360 Vendor")
                        .setContentText(user_name + " Welcome to APT360 Vendor")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setGroupSummary(true)
                        .setGroup("APT360 Vendor");

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}