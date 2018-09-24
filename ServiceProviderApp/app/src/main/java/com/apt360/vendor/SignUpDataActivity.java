package com.apt360.vendor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.connection.ConnectionDetector;
import com.google.firebase.iid.FirebaseInstanceId;
import com.heetch.countrypicker.Country;
import com.heetch.countrypicker.CountryPickerCallbacks;
import com.heetch.countrypicker.CountryPickerDialog;
import com.heetch.countrypicker.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import services.Services;

/**
 * Created by Piyush on 10/31/2017.
 */

public class SignUpDataActivity extends AppCompatActivity {

    TextInputEditText txtContact, txtEmail, txtName;
    Button btnCreatePasscode;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private KProgressHUD hud;
    RequestQueue requestQueue;
    String user_id = "";
    SharedPreferences preferences;
    CircularImageView imgUserImage;
    LinearLayout countryblock;
    TextView countryCode;
    ImageView countryFlag;
    String mUserID;
    Bitmap bitmap;
    String facebookUserID, facebookUserFirstName, facebookUserLastName, facebookUserPic, facebookUserEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_data_activity);
        preferences = PreferenceManager.getDefaultSharedPreferences(SignUpDataActivity.this);
        /*creating connection detector class instance*/
        cd = new ConnectionDetector(SignUpDataActivity.this);
        isInternetPresent = cd.isConnectingToInternet();

        Intent userID = getIntent();
        mUserID = userID.getStringExtra("USER_ID");

        initView();

        facebookUserID = preferences.getString("FacebookUserID", "");
        facebookUserFirstName = preferences.getString("FacebookUserFirstName", "");
        facebookUserLastName = preferences.getString("FacebookUserLastName", "");
        facebookUserPic = preferences.getString("FacebookUserPic", "");
        facebookUserEmail = preferences.getString("FacebookUserEmail", "");

        txtName.setText(facebookUserFirstName + " " + facebookUserLastName);
        txtEmail.setText(facebookUserEmail);
        Picasso.with(getApplicationContext()).load(facebookUserPic).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder)
                .into(imgUserImage);



        final CountryPickerDialog countryPicker =
                new CountryPickerDialog(SignUpDataActivity.this, new CountryPickerCallbacks() {
                    @Override
                    public void onCountrySelected(Country country, int flagResId) {
                        // TODO handle callback
                        countryCode.setText("+" + country.getDialingCode());
                        countryFlag.setImageDrawable(getResources().getDrawable(flagResId));
                    }
                });
        countryPicker.setTitle("Select Country");

        Country country = countryPicker.getCountryFromSIM(SignUpDataActivity.this);
        countryCode.setText("+" + country.getDialingCode());
        countryFlag.setImageDrawable(getResources().getDrawable(Utils.getMipmapResId(SignUpDataActivity.this,
                country.getIsoCode().toLowerCase(Locale.ENGLISH) + "_flag")));

        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int screenWidth = displaymetrics.widthPixels;
        final int screenHeight = displaymetrics.heightPixels;


        countryblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryPicker.show();
                countryPicker.getWindow().setLayout(screenWidth, screenHeight);
            }
        });

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
                    } else if (!isValidMobile(txtContact.getText().toString())) {
                        txtContact.setError("Mobile no is not valid!");
                        txtContact.requestFocus();
                    } else if (txtContact.getText().toString().length() < 10) {
                        txtContact.setError("Enter valid 10 digit mobile no");
                        txtContact.requestFocus();
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

        imgUserImage.buildDrawingCache(true);
        bitmap = imgUserImage.getDrawingCache(true);

        TelephonyManager tm = (TelephonyManager) this.getSystemService(SignUpDataActivity.this.TELEPHONY_SERVICE);
        final String countryCodeValue = tm.getNetworkCountryIso();

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
                                editor.putString("login", "Social");
                                editor.putString("user_data", jObj.getString("user_data"));
                                editor.commit();

                                if (hud.isShowing())
                                    hud.dismiss();

                                editor.commit();
                                Intent intent = new Intent(SignUpDataActivity.this, MainActivity.class);
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
                        Log.d("Error.Response", error.toString());
                        //Toast.makeText(SignUpDataActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Service", "social_login_data");
                params.put("mobile_no", countryCode.getText().toString() + txtContact.getText().toString());
                params.put("email", txtEmail.getText().toString());
                params.put("user_name", txtName.getText().toString());
                params.put("device_id", FirebaseInstanceId.getInstance().getToken());
                params.put("device_type", "1");
                params.put("profile_picture", getEncoded64ImageStringFromBitmap(bitmap));
                params.put("user_id",mUserID);
                params.put("user_type","1");
                params.put("country",countryCodeValue.toUpperCase());
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(SignUpDataActivity.this);
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }

    private void initView() {
        imgUserImage = (CircularImageView) findViewById(R.id.imgUserImage);
        txtContact = (TextInputEditText) findViewById(R.id.txtContact);
        txtEmail = (TextInputEditText) findViewById(R.id.txtEmail);
        txtName = (TextInputEditText) findViewById(R.id.txtUserName);
        btnCreatePasscode = (Button) findViewById(R.id.btnCreatePasscode);
        countryblock = (LinearLayout) findViewById(R.id.countryblock);
        countryFlag = (ImageView) findViewById(R.id.countryFlag);
        countryCode = (TextView) findViewById(R.id.countryCode);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }
    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
    }
}
