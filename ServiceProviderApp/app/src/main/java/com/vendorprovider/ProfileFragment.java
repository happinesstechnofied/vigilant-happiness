package com.vendorprovider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import kprogresshud.KProgressHUD;
import services.Services;

public class ProfileFragment extends Fragment {

    RequestQueue requestQueue;
    TextInputEditText txtUserName, txtContact, txtEmail, txtAddress, txtCity, txtPincode;
    TextView txtName;
    String user_id, user_name, mobile_no, email, address, city, pincode;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private KProgressHUD hud;
    public static CircularImageView imgUserImage;
    String userImage;
    private Button btnUpdate;
    public static Bitmap userPhotoPath = null;
    public static int checkPhoto = 0;
    public static Boolean isImageUpload = false;
    public static TextView txtUploadPhoto;
    private RelativeLayout photoLayout;
    SharedPreferences preferences;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        /*creating connection detector class instance*/
        cd = new ConnectionDetector(getContext());
        isInternetPresent = cd.isConnectingToInternet();

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        initView(view);

        if (preferences.getString("user_data", "") != null && !preferences.getString("user_data", "").equalsIgnoreCase("")) {
            try {
                JSONObject jObj = new JSONObject(preferences.getString("user_data", ""));

                if (!jObj.getString("profile_picture").equalsIgnoreCase("")) {
                    URL url = new URL(jObj.getString("profile_picture"));
                    //imgUserImage.setImageBitmap(getBitmapFromURL(url));
                    new ImageLoadTask(url, imgUserImage).execute();
                }
                
                user_id = jObj.getString("user_id");
                txtName.setText(jObj.getString("user_name"));
                txtUserName.setText(jObj.getString("user_name"));
                txtAddress.setText(jObj.getString("address"));
                txtCity.setText(jObj.getString("city"));
                txtPincode.setText(jObj.getString("pincode"));
                txtEmail.setText(jObj.getString("email"));
                txtContact.setText(jObj.getString("mobile_no"));

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }


        imgUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("sp_from", "profile");
                editor.commit();

                new BottomDialogActivity().show(getFragmentManager(), "Dialog");
            }
        });


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                user_name = txtUserName.getText().toString();
                mobile_no = txtContact.getText().toString();
                email = txtEmail.getText().toString();
                address = txtAddress.getText().toString();
                city = txtCity.getText().toString();
                pincode = txtPincode.getText().toString();

                // Reset errors.
                txtUserName.setError(null);
                txtContact.setError(null);
                txtEmail.setError(null);


                // Check input value...
                if (TextUtils.isEmpty(user_name)) {
                    txtUserName.setError("Please enter your user name");
                    txtUserName.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(mobile_no)) {
                    txtContact.setError("Please enter your mobile no");
                    txtContact.requestFocus();
                    return;
                } else if (!isValidMobile(mobile_no)) {
                    txtContact.setError("Mobile no is not valid!");
                    txtContact.requestFocus();
                } else if (TextUtils.isEmpty(email)) {
                    txtEmail.setError("Please enter your email");
                    txtEmail.requestFocus();
                    return;
                } else if (!isEmailValid(email)) {
                    txtEmail.setError("Email is not valid!");
                    txtEmail.requestFocus();
                } else {
                    if (isInternetPresent) {
                        changeProfile();
                    } else {
                        Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_more).setVisible(false);
        menu.findItem(R.id.action_logout).setVisible(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                preferences.edit().remove("user_data").commit();

                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                Toast.makeText(getContext(), "Logout successfully", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView(View view) {

        txtName = (TextView) view.findViewById(R.id.txtDisplayName);
        txtUserName = (TextInputEditText) view.findViewById(R.id.txtUserName);
        txtContact = (TextInputEditText) view.findViewById(R.id.txtContact);
        txtEmail = (TextInputEditText) view.findViewById(R.id.txtEmail);
        txtAddress = (TextInputEditText) view.findViewById(R.id.txtAddress);
        txtCity = (TextInputEditText) view.findViewById(R.id.txtCity);
        txtPincode = (TextInputEditText) view.findViewById(R.id.txtPincode);

        imgUserImage = (CircularImageView) view.findViewById(R.id.imgUserImage);
        btnUpdate = (Button) view.findViewById(R.id.btnUpdate);

    }

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
    }

    private void changeProfile() {

        hud = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Creating user...");
        hud.show();

        imgUserImage.buildDrawingCache(true);
        final Bitmap bitmap = imgUserImage.getDrawingCache(true);
        getEncoded64ImageStringFromBitmap(bitmap);


//        BitmapDrawable drawable = (BitmapDrawable)imgUserImage.getDrawable();
//        Bitmap bitmap = drawable.getBitmap();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.CHANGE_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jObj = new JSONObject(response.toString());
                            String status = jObj.getString("status");
                            if (status.matches("success")) {

                                JSONObject innerjObj = new JSONObject(jObj.getString("user_data"));
                                txtName.setText(innerjObj.getString("user_name"));
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("user_data", jObj.getString("user_data"));
                                editor.commit();

                                if (hud.isShowing())
                                    hud.dismiss();

                                Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_LONG).show();

                            } else {
                                if (hud.isShowing())
                                    hud.dismiss();

                                String failed = jObj.getString("message");
                                Toast.makeText(getContext(), failed, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Service", "change_profile");
                params.put("user_id", user_id);
                params.put("user_name", user_name);
                params.put("mobile_no", "+91" + mobile_no);
                params.put("email", email);
                params.put("address", address);
                params.put("city", city);
                params.put("pincode", pincode);
                params.put("profile_picture", getEncoded64ImageStringFromBitmap(bitmap));
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(getContext());
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }


    public static Bitmap getBitmapFromURL(URL url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap", "returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception", e.getMessage());
            return null;
        }
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private URL url;
        private ImageView imageView;

        public ImageLoadTask(URL url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = url;
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

}