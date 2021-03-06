package com.apt360.vendor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.connection.ConnectionDetector;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.heetch.countrypicker.Country;
import com.heetch.countrypicker.CountryPickerCallbacks;
import com.heetch.countrypicker.CountryPickerDialog;
import com.heetch.countrypicker.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

import model.User;
import services.Services;
import util.SharedPrefManager;

public class LoginActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    TextInputEditText txtContact, txtPassword;
    String mobile_no, password;
    LinearLayout countryblock;
    TextView countryCode;
    ImageView countryFlag;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private KProgressHUD hud;
    private ImageView btnFacebook;
    private ImageView btnGoogle;
    private Button btnSignIn;
    private Button btnSignUp;
    private TextView txtForgotPassword;
    SharedPreferences preferences;
    CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.login_activity);
        preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        Locale locale = Locale.getDefault();


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.apt360.vendor",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        initView();

        final CountryPickerDialog countryPicker =
                new CountryPickerDialog(LoginActivity.this, new CountryPickerCallbacks() {
                    @Override
                    public void onCountrySelected(Country country, int flagResId) {
                        // TODO handle callback
                        countryCode.setText("+" + country.getDialingCode());
                        countryFlag.setImageDrawable(getResources().getDrawable(flagResId));
                    }
                });
        countryPicker.setTitle("Select Country");


        Country country = countryPicker.getCountryFromSIM(LoginActivity.this);


        countryCode.setText("+" + country.getDialingCode());
        countryFlag.setImageDrawable(getResources().getDrawable(Utils.getMipmapResId(LoginActivity.this,
                country.getIsoCode().toLowerCase(Locale.ENGLISH) + "_flag")));


        DisplayMetrics displaymetrics = new DisplayMetrics();
        LoginActivity.this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int screenWidth = displaymetrics.widthPixels;
        final int screenHeight = displaymetrics.heightPixels;


        countryblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryPicker.show();
                countryPicker.getWindow().setLayout(screenWidth, screenHeight);
            }
        });


        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                getFacebookUserDetails(loginResult);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(LoginActivity.this, "Login with correct data", Toast.LENGTH_LONG).show();

            }
        });


        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        // [END configure_signin]


        // [START build_client]
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END build_client]

        // If you are using in a fragment, call loginButton.setFragment(this);
        //setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("User Login");

        /*creating connection detector class instance*/
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        btnFacebook.setImageDrawable(getState(R.drawable.ic_facebook_default, R.drawable.ic_facebook_click));
        btnGoogle.setImageDrawable(getState(R.drawable.ic_google_default, R.drawable.ic_google_click));

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternetPresent) {
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
                } else {
                    Toast.makeText(LoginActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternetPresent) {
                    getGoogleUserDetails();
                } else {
                    Toast.makeText(LoginActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUp = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(signUp);
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //first getting the values

                mobile_no = txtContact.getText().toString();
                password = txtPassword.getText().toString();

                // Reset errors.
                txtContact.setError(null);
                txtPassword.setError(null);

                // Check input value...
                if (TextUtils.isEmpty(mobile_no)) {
                    txtContact.setError("Please enter your mobile no");
                    txtContact.requestFocus();
                    return;
                } else if (!isValidMobile(mobile_no)) {
                    txtContact.setError("Mobile no is not valid!");
                    txtContact.requestFocus();
                } else if (mobile_no.length() < 10) {
                    txtContact.setError("Enter valid 10 digit mobile no");
                    txtContact.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    txtPassword.setError("Please enter your password");
                    txtPassword.requestFocus();
                    return;
                } else {
                    if (isInternetPresent) {
                        UserLogin();
                    } else {
                        Toast.makeText(LoginActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }

//                Intent signUp = new Intent(LoginActivity.this,MainActivity.class);
//                signUp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(signUp);
//                finish();
            }
        });
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivity(intent);
            }
        });


    }

    private void initView() {
        countryblock = (LinearLayout) findViewById(R.id.countryblock);
        countryFlag = (ImageView) findViewById(R.id.countryFlag);
        countryCode = (TextView) findViewById(R.id.countryCode);

        txtContact = (TextInputEditText) findViewById(R.id.txtContact);
        txtPassword = (TextInputEditText) findViewById(R.id.txtPassword);
        btnFacebook = (ImageView) findViewById(R.id.btnFacebook);
        btnGoogle = (ImageView) findViewById(R.id.btnGoogle);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);
    }

    private Drawable getState(int img_w, int img_b) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, getResources().getDrawable(img_b));
        states.addState(new int[]{android.R.attr.state_focused}, getResources().getDrawable(img_b));
        states.addState(new int[]{}, getResources().getDrawable(img_w));
        return states;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> result = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(result);


        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getGoogleUserDetails() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void getFacebookUserDetails(LoginResult loginResult) {
        GraphRequest data_request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject json_object, GraphResponse response) {
                        try {
                            JSONObject obj = new JSONObject(json_object.toString());
                            String facebookUserEmail = "";
                            if (obj.has("email")) {
                                facebookUserEmail = obj.get("email").toString();
                            }
                            String facebookUserId = obj.get("id").toString();
                            String facebookUserFirstName = obj.get("first_name").toString();
                            String facebookUserLastName = obj.get("last_name").toString();
                            JSONObject imgObj = obj.getJSONObject("picture").getJSONObject("data");
                            String mUserPicSocial = imgObj.getString("url");

                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("FacebookUserID", facebookUserId);
                            editor.putString("FacebookUserFirstName", facebookUserFirstName);
                            editor.putString("FacebookUserLastName", facebookUserLastName);
                            editor.putString("FacebookUserPic", mUserPicSocial);
                            editor.putString("FacebookUserEmail", facebookUserEmail);
                            editor.commit();

                            hud = KProgressHUD.create(LoginActivity.this)
                                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                    .setLabel("Please wait");
                            hud.show();
                            getFacebookData(facebookUserId, facebookUserEmail, facebookUserFirstName + " " + facebookUserLastName, mUserPicSocial, "facebook");

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Login fail please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id, name, email, first_name, last_name, picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();
    }

    private void getFacebookData(String facebookUserId, final String facebookUserEmail, final String user_name, String facebookUserPic, final String facebook) {

        TelephonyManager tm = (TelephonyManager) this.getSystemService(LoginActivity.this.TELEPHONY_SERVICE);
        final String countryCodeValue = tm.getNetworkCountryIso();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.SIGN_SOCIAL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response.toString());
                            String status = jObj.getString("status");
                            if (status.matches("success")) {

                                JSONObject jsonObj = new JSONObject(jObj.getString("user_data"));
                                String mUserMobileNo = jsonObj.getString("mobile_no");

                                if (hud.isShowing())
                                    hud.dismiss();

                                if (mUserMobileNo == null || mUserMobileNo.equalsIgnoreCase("null") || mUserMobileNo.equalsIgnoreCase("")) {
                                    Intent signUp = new Intent(LoginActivity.this, SignUpDataActivity.class);
                                    signUp.putExtra("USER_ID", jsonObj.getString("user_id"));
                                    signUp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(signUp);

                                } else {
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("USER_ID", jsonObj.getString("user_id"));
                                    editor.putString("login", "Social");
                                    editor.putString("user_data", jObj.getString("user_data"));
                                    editor.commit();

                                    Intent signUp = new Intent(LoginActivity.this, MainActivity.class);
                                    signUp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(signUp);

                                }
                                finish();

                            } else {
                                if (hud.isShowing())
                                    hud.dismiss();
                                Toast.makeText(LoginActivity.this, "** I'd or Password does not match!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            if (hud.isShowing())
                                hud.dismiss();
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
                        //Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Service", "sign_up_social_sp");
                params.put("email", facebookUserEmail);
                params.put("user_name", user_name);
                params.put("device_id", FirebaseInstanceId.getInstance().getToken());
                params.put("device_type", "1");
                params.put("user_type", "1");
                params.put("country", countryCodeValue.toUpperCase());

                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(postRequest);

    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);
            hud = KProgressHUD.create(LoginActivity.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait");
            hud.show();

            SharedPreferences.Editor editor = preferences.edit();

            if (acct.getPhotoUrl() == null || acct.getPhotoUrl().toString().equalsIgnoreCase("")) {
                editor.putString("FacebookUserID", acct.getId());
                editor.putString("FacebookUserFirstName", acct.getGivenName());
                editor.putString("FacebookUserLastName", acct.getFamilyName());
                editor.putString("FacebookUserPic", "http://orbrixtechnologies.com/services/wp-content/uploads/2018/08/placeholder_profile-200x200.png");
                editor.putString("FacebookUserEmail", acct.getEmail());
                getFacebookData(acct.getEmail(), acct.getEmail(), acct.getGivenName() + " " + acct.getFamilyName(), "http://orbrixtechnologies.com/services/wp-content/uploads/2018/08/placeholder_profile-200x200.png", "gPlus");
            } else {
                editor.putString("FacebookUserID", acct.getId());
                editor.putString("FacebookUserFirstName", acct.getGivenName());
                editor.putString("FacebookUserLastName", acct.getFamilyName());
                editor.putString("FacebookUserPic", acct.getPhotoUrl().toString());
                editor.putString("FacebookUserEmail", acct.getEmail());
                getFacebookData(acct.getEmail(), acct.getEmail(), acct.getGivenName() + " " + acct.getFamilyName(), acct.getPhotoUrl().toString(), "gPlus");

            }
            editor.commit();

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            //Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(LoginActivity.this, "signInResult:failed code=" + e.getStatusCode(), Toast.LENGTH_LONG).show();
            //updateUI(null);
        }

    }

    private void UserLogin() {

        hud = KProgressHUD.create(LoginActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Sing In...");
        hud.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.SIGN_IN_URL,
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

                                Intent signUp = new Intent(LoginActivity.this, MainActivity.class);
                                signUp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(signUp);
                                finish();

                            } else {
                                if (hud.isShowing())
                                    hud.dismiss();

                                String failed = jObj.getString("message");
                                Toast.makeText(LoginActivity.this, failed, Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Service", "login");
                params.put("mobile_no", countryCode.getText().toString() + mobile_no);
                params.put("password", password);
                params.put("device_id", "1234567890");
                params.put("device_type", "1");
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(LoginActivity.this);
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }
}