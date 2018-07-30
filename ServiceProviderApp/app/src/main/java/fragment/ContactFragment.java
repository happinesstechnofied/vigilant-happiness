package fragment;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.connection.ConnectionDetector;
import com.heetch.countrypicker.Country;
import com.heetch.countrypicker.CountryPickerCallbacks;
import com.heetch.countrypicker.CountryPickerDialog;
import com.heetch.countrypicker.Utils;
import com.vendorprovider.LoginActivity;
import com.vendorprovider.MainActivity;
import com.vendorprovider.MapLocationActivity;
import com.vendorprovider.ProfileFragment;
import com.vendorprovider.R;
import com.vendorprovider.ServiceCreationActivity;
import com.vendorprovider.ViewServicesActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import kprogresshud.KProgressHUD;
import services.Services;

import static android.widget.Toast.LENGTH_SHORT;

public class ContactFragment extends Fragment {
    public static ContactFragment newInstance() {
        ContactFragment fragment = new ContactFragment();
        return fragment;
    }

    private static final int READ_STORAGE_CODE = 1001;
    private KProgressHUD hud;
    RequestQueue requestQueue;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    Button btnSubmit;
    Button btnBack;
    SharedPreferences preferences;
    public static String latitude_location;
    public static String longitude_location;
    TextInputEditText txtVendorName, txtVendorApartment, txtVendorNumber, txtVendorEmail;
    public static TextInputEditText txtVendorAddress;
    public static TextInputEditText txtVendorCity;
    public static TextInputEditText txtVendorPincode;
    String vendor_name, vendor_apartment, vendor_mobile_no, vendor_email, vendor_address, vendor_city, vendor_pincode;
    String main_category, sub_category, main_category_name, sub_category_name, service_title, images, tag_line;
    String sort_description, opening_hours, closing_hours, mrp_price, sale_price, payment_option, payment_type;
    String feature_post;
    String genericKeyword;
    String checkEditMode;
    Button btnLocation;
    int position;
    LinearLayout countryblock;
    TextView countryCode;
    ImageView countryFlag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_detials_fragment, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        checkEditMode = preferences.getString("EditService", "");
        position = preferences.getInt("Position", 0);


        main_category = preferences.getString("main_category", "");
        sub_category = preferences.getString("sub_category", "");
        main_category_name = preferences.getString("main_category_name", "");
        sub_category_name = preferences.getString("sub_category_name", "");
        service_title = preferences.getString("service_title", "");
        tag_line = preferences.getString("tag_line", "");
        images = preferences.getString("images", "");
        sort_description = preferences.getString("sort_description", "");
        opening_hours = preferences.getString("opening_hours", "");
        closing_hours = preferences.getString("closing_hours", "");
        mrp_price = preferences.getString("mrp_price", "");
        sale_price = preferences.getString("sale_price", "");
        payment_option = preferences.getString("payment_option", "");
        payment_type = preferences.getString("payment_type", "");
        feature_post = preferences.getString("feature_post", "");
        genericKeyword = preferences.getString("generic_keyword", "");

        cd = new ConnectionDetector(getContext());
        isInternetPresent = cd.isConnectingToInternet();

        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        btnBack = (Button) view.findViewById(R.id.btnBack);
        txtVendorName = (TextInputEditText) view.findViewById(R.id.txtVendorName);
        txtVendorApartment = (TextInputEditText) view.findViewById(R.id.txtVendorApartment);
        txtVendorNumber = (TextInputEditText) view.findViewById(R.id.txtVendorNumber);
        txtVendorEmail = (TextInputEditText) view.findViewById(R.id.txtVendorEmail);
        txtVendorAddress = (TextInputEditText) view.findViewById(R.id.txtVendorAddress);
        txtVendorCity = (TextInputEditText) view.findViewById(R.id.txtVendorCity);
        txtVendorPincode = (TextInputEditText) view.findViewById(R.id.txtVendorPincode);
        btnLocation = (Button) view.findViewById(R.id.btnLocation);
        countryblock = (LinearLayout) view.findViewById(R.id.countryblock);
        countryFlag = (ImageView) view.findViewById(R.id.countryFlag);
        countryCode = (TextView) view.findViewById(R.id.countryCode);

        final CountryPickerDialog countryPicker =
                new CountryPickerDialog(getContext(), new CountryPickerCallbacks() {
                    @Override
                    public void onCountrySelected(Country country, int flagResId) {
                        // TODO handle callback
                        countryCode.setText("+" + country.getDialingCode());
                        countryFlag.setImageDrawable(getResources().getDrawable(flagResId));
                    }
                });
        countryPicker.setTitle("Select Country");


        Country country = countryPicker.getCountryFromSIM(getContext());
        countryCode.setText("+" + country.getDialingCode());
        countryFlag.setImageDrawable(getResources().getDrawable(Utils.getMipmapResId(getContext(),
                country.getIsoCode().toLowerCase(Locale.ENGLISH) + "_flag")));

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int screenWidth = displaymetrics.widthPixels;
        final int screenHeight = displaymetrics.heightPixels;


        countryblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryPicker.show();
                countryPicker.getWindow().setLayout(screenWidth, screenHeight);
            }
        });

        if (checkEditMode.equals("Edit")) {
            txtVendorName.setText(ViewServicesActivity.arrayList.get(position).getContactName());
            txtVendorApartment.setText(ViewServicesActivity.arrayList.get(position).getAppartment());
            txtVendorNumber.setText(ViewServicesActivity.arrayList.get(position).getMobileNo());
            txtVendorEmail.setText(ViewServicesActivity.arrayList.get(position).getEmail());
            txtVendorAddress.setText(ViewServicesActivity.arrayList.get(position).getAddress());
            txtVendorCity.setText(ViewServicesActivity.arrayList.get(position).getCity());
            txtVendorPincode.setText(ViewServicesActivity.arrayList.get(position).getPincode());
        }

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MapLocationActivity.class));
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vendor_name = txtVendorName.getText().toString();
                vendor_apartment = txtVendorApartment.getText().toString();
                vendor_mobile_no = txtVendorNumber.getText().toString();
                vendor_email = txtVendorEmail.getText().toString();
                vendor_address = txtVendorAddress.getText().toString();
                vendor_city = txtVendorCity.getText().toString();
                vendor_pincode = txtVendorPincode.getText().toString();

                if (TextUtils.isEmpty(vendor_name)) {
                    txtVendorName.setError("Please enter name!");
                    txtVendorName.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(vendor_apartment)) {
                    txtVendorApartment.setError("Please enter apartment!");
                    txtVendorApartment.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(vendor_mobile_no)) {
                    txtVendorNumber.setError("Please enter number!");
                    txtVendorNumber.requestFocus();
                    return;

                }
                if (TextUtils.isEmpty(vendor_email)) {
                    txtVendorEmail.setError("Please enter email!");
                    txtVendorEmail.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(latitude_location)) {
                    Toast toast = Toast.makeText(getContext(), "Please select location!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                } else if (!isEmailValid(vendor_email)) {
                    txtVendorEmail.setError("Please enter valid email!");
                    txtVendorEmail.requestFocus();
                } else {
                    CreateServiceData();
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceCreationActivity.mViewPager.setCurrentItem(4, true);
            }
        });
        return view;
    }

    private void CreateServiceData() {
        hud = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Creating...");
        hud.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.SERVICE_CREATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (hud.isShowing()) {
                            hud.dismiss();
                        }
                        try {
                            JSONObject jObj = new JSONObject(response.toString());
                            String status = jObj.getString("status");
                            if (status.matches("success")) {
                                //starting the profile activity
                                ServiceCreationActivity.mViewPager.setCurrentItem(6, true);
                            } else {
                                String failed = jObj.getString("message");
                                Toast.makeText(getContext(), failed, LENGTH_SHORT).show();
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
                        if (hud.isShowing()) {
                            hud.dismiss();
                        }
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(getContext(), error.toString(), LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Service", "create_service");

                if (preferences.getString("user_data", "") != null && !preferences.getString("user_data", "").equalsIgnoreCase("")) {
                    try {
                        JSONObject jObj = new JSONObject(preferences.getString("user_data", ""));
                        params.put("user_id", jObj.getString("user_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }


                params.put("main_category", main_category);
                params.put("sub_category", sub_category);
                params.put("main_category_name", main_category_name);
                params.put("sub_category_name", sub_category_name);
                params.put("service_title", service_title);
                params.put("tag_line", tag_line);

                int image_counter = 0;
                do {
                    params.put("image_" + image_counter, preferences.getString("image_" + image_counter, ""));
                    image_counter++;
                } while (!preferences.getString("image_" + image_counter, "").equalsIgnoreCase(""));

                params.put("sort_description", sort_description);
                params.put("opening_hours", opening_hours);
                params.put("closing_hours", closing_hours);
                params.put("mrp_price", mrp_price);
                params.put("sale_price", sale_price);
                params.put("payment_option", payment_option);
                params.put("payment_type", payment_type);

                params.put("feature_0", preferences.getString("feature_0", ""));

                params.put("tags", genericKeyword);
                params.put("vendor_name", vendor_name);
                params.put("vendor_apartment", vendor_apartment);
                params.put("vendor_latitude", latitude_location);
                params.put("vendor_longitude", longitude_location);
                params.put("vendor_mobile_no", countryCode.getText().toString()+""+vendor_mobile_no);
                params.put("vendor_email", vendor_email);
                params.put("vendor_address", vendor_address);
                params.put("vendor_city", vendor_city);
                params.put("vendor_pincode", vendor_pincode);
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(getContext());
        postRequest.setShouldCache(false);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(postRequest);
    }

    boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }

    private boolean isPermissionGranted(String permission) {
        int result = ContextCompat.checkSelfPermission(getContext(), permission);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }

    //Requesting permission
    private void requestPermission(String permission, int code) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
        }
        ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, code);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_more).setVisible(true);
        menu.findItem(R.id.action_logout).setVisible(false);
    }
}