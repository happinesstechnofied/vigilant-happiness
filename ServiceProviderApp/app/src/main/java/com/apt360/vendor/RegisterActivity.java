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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import services.Services;

public class RegisterActivity extends AppCompatActivity {

    LinearLayout countryblock;
    TextView countryCode;
    ImageView countryFlag;

    RequestQueue requestQueue;
    TextInputEditText txtUserName, txtContact, txtEmail, txtPassword, txtConPassword;
    String user_name, mobile_no, email, password, con_password;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private KProgressHUD hud;
    public static CircularImageView imgUserImage;
    String userImage;
    private Button btnRegisterNow;
    public static Bitmap userPhotoPath = null;
    public static int checkPhoto = 0;
    public static Boolean isImageUpload = false;
    public static TextView txtUploadPhoto;
    private RelativeLayout photoLayout;
    SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        initView();

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("User Register");

        /*creating connection detector class instance*/
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        final CountryPickerDialog countryPicker =
                new CountryPickerDialog(RegisterActivity.this, new CountryPickerCallbacks() {
                    @Override
                    public void onCountrySelected(Country country, int flagResId) {
                        // TODO handle callback
                        countryCode.setText("+" + country.getDialingCode());
                        countryFlag.setImageDrawable(getResources().getDrawable(flagResId));
                    }
                });
        countryPicker.setTitle("Select Country");


        Country country = countryPicker.getCountryFromSIM(RegisterActivity.this);
        countryCode.setText("+" + country.getDialingCode());
        countryFlag.setImageDrawable(getResources().getDrawable(Utils.getMipmapResId(RegisterActivity.this,
                country.getIsoCode().toLowerCase(Locale.ENGLISH) + "_flag")));

        DisplayMetrics displaymetrics = new DisplayMetrics();
        RegisterActivity.this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int screenWidth = displaymetrics.widthPixels;
        final int screenHeight = displaymetrics.heightPixels;


        countryblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryPicker.show();
                countryPicker.getWindow().setLayout(screenWidth, screenHeight);
            }
        });


        btnRegisterNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //first getting the values
                user_name = txtUserName.getText().toString();
                mobile_no = txtContact.getText().toString();
                email = txtEmail.getText().toString();
                password = txtPassword.getText().toString();
                con_password = txtConPassword.getText().toString();

                // Reset errors.
                txtUserName.setError(null);
                txtContact.setError(null);
                txtEmail.setError(null);
                txtPassword.setError(null);
                txtConPassword.setError(null);
                txtUploadPhoto.setError(null);

                // Check input value...
                if (TextUtils.isEmpty(mobile_no)) {
                    txtContact.setError("Please enter your mobile no");
                    txtContact.requestFocus();
                    return;
                } else if (!isValidMobile(mobile_no)) {
                    txtContact.setError("Mobile no is not valid!");
                    txtContact.requestFocus();
                }else if (mobile_no.length() < 10) {
                    txtContact.setError("Enter valid 10 digit mobile no");
                    txtContact.requestFocus();
                    return;
                }  else if (TextUtils.isEmpty(email)) {
                    txtEmail.setError("Please enter your email");
                    txtEmail.requestFocus();
                    return;
                } else if (!isEmailValid(email)) {
                    txtEmail.setError("Email is not valid!");
                    txtEmail.requestFocus();
                } else if (TextUtils.isEmpty(user_name)) {
                    txtUserName.setError("Please enter your name");
                    txtUserName.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    txtPassword.setError("Please enter your password");
                    txtPassword.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(con_password)) {
                    txtConPassword.setError("Please enter your confirm password");
                    txtConPassword.requestFocus();
                    return;
                } else if (!password.matches(con_password)) {
                    txtConPassword.setError("Does not match confirm password");
                    txtConPassword.requestFocus();
                } else if (!isImageUpload) {
                    txtUploadPhoto.setError("Please upload your profile picture");
                    txtUploadPhoto.requestFocus();
                } else {
                    if (isInternetPresent) {
                        UserRegister();
                    } else {
                        Toast.makeText(RegisterActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
        photoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("sp_from", "register");
                editor.commit();
                new BottomDialogActivity().show(getSupportFragmentManager(), "Dialog");
            }
        });
    }

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }


    private void UserRegister() {

        hud = KProgressHUD.create(RegisterActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Creating user...");
        hud.show();

        TelephonyManager tm = (TelephonyManager) this.getSystemService(RegisterActivity.this.TELEPHONY_SERVICE);
        final String countryCodeValue = tm.getNetworkCountryIso();

        imgUserImage.buildDrawingCache(true);
        final Bitmap bitmap = imgUserImage.getDrawingCache(true);
        getEncoded64ImageStringFromBitmap(bitmap);

//        BitmapDrawable drawable = (BitmapDrawable)imgUserImage.getDrawable();
//        Bitmap bitmap = drawable.getBitmap();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.SIGN_UP_URL,
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

                                Intent intent = new Intent(RegisterActivity.this, OTPVerifyActivity.class);
                                intent.putExtra("intent_from", "registration");
                                intent.putExtra("user_id", USER_ID);
                                intent.putExtra("pincode", pincode);
                                startActivity(intent);

                                txtContact.setText("");
                                txtEmail.setText("");
                                txtUserName.setText("");
                                txtPassword.setText("");
                                txtConPassword.setText("");

                            } else {
                                if (hud.isShowing())
                                    hud.dismiss();

                                String failed = jObj.getString("message");
                                Toast.makeText(RegisterActivity.this, failed, Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(RegisterActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Service", "send_otp");
                params.put("user_name", user_name);
                params.put("mobile_no", countryCode.getText().toString() + mobile_no);
                params.put("email", email);
                params.put("password", password);
                params.put("device_id", FirebaseInstanceId.getInstance().getToken());
                params.put("device_type", "1");
                params.put("profile_image", getEncoded64ImageStringFromBitmap(bitmap));
                params.put("user_type","1");
                params.put("country",countryCodeValue.toUpperCase());
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(RegisterActivity.this);
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }

    private void initView() {
        countryblock = (LinearLayout) findViewById(R.id.countryblock);
        countryFlag = (ImageView) findViewById(R.id.countryFlag);
        countryCode = (TextView) findViewById(R.id.countryCode);

        txtUserName = (TextInputEditText) findViewById(R.id.txtUserName);
        txtContact = (TextInputEditText) findViewById(R.id.txtContact);
        txtEmail = (TextInputEditText) findViewById(R.id.txtEmail);
        txtPassword = (TextInputEditText) findViewById(R.id.txtPassword);
        txtConPassword = (TextInputEditText) findViewById(R.id.txtConPassword);

        imgUserImage = (CircularImageView) findViewById(R.id.imgUserImage);
        btnRegisterNow = (Button) findViewById(R.id.btnRegisterNow);
        txtUploadPhoto = (TextView) findViewById(R.id.txtUploadPhoto);
        photoLayout = (RelativeLayout) findViewById(R.id.photoLayout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }
}