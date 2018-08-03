package com.subscriber;

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


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import connection.ConnectionDetector;
import kprogresshud.KProgressHUD;
import services.Services;

/**
 * Created by Piyush on 10/31/2017.
 */

public class ChangeForgotActivity extends AppCompatActivity {

    TextInputEditText editNewPassword, editConfirmPassword;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_forgot_activity);

        /*creating connection detector class instance*/
        cd = new ConnectionDetector(ChangeForgotActivity.this);
        isInternetPresent = cd.isConnectingToInternet();

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");

        initView();

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create New Password");

        btnCreatePasscode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newPassword = editNewPassword.getText().toString();
                newConfirmPassword = editConfirmPassword.getText().toString();

                if (isInternetPresent) {
                    // Check input value...
                    if (TextUtils.isEmpty(newPassword)) {
                        editNewPassword.setError("Please enter your new password");
                        editNewPassword.requestFocus();
                        return;
                    }
                    if (TextUtils.isEmpty(newConfirmPassword)) {
                        editConfirmPassword.setError("Please enter your confirm password");
                        editConfirmPassword.requestFocus();
                        return;
                    }
                    // check confirm password match or not
                    if (!newPassword.matches(newConfirmPassword)) {
                        editConfirmPassword.setError("Does't match confirm password");
                        editConfirmPassword.requestFocus();
                    } else {
                        CreateNewPasscode(user_id);
                        hud = KProgressHUD.create(ChangeForgotActivity.this)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setLabel("Please wait");
                        hud.show();
                    }
                } else {
                    Toast.makeText(ChangeForgotActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void CreateNewPasscode(final String user_id) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.CHANGE_FORGOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response.toString());
                            String status = jObj.getString("status");
                            if (status.matches("success")) {
                                if (hud.isShowing())
                                    hud.dismiss();
                                Toast.makeText(ChangeForgotActivity.this, "Password Changed Successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            } else {
                                if (hud.isShowing())
                                    hud.dismiss();
                                String failed = jObj.getString("message");
                                Toast.makeText(ChangeForgotActivity.this, failed, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ChangeForgotActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Service", "change_forgot_password");
                params.put("user_id", user_id);
                params.put("newpassword", newConfirmPassword);
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(ChangeForgotActivity.this);
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }

    private void initView() {
        editNewPassword = (TextInputEditText) findViewById(R.id.editNewPassword);
        editConfirmPassword = (TextInputEditText) findViewById(R.id.editConfirmPassword);
        btnCreatePasscode = (Button) findViewById(R.id.btnCreatePasscode);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}
