package fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.vendorprovider.CategoriesActivity;
import com.vendorprovider.R;
import com.vendorprovider.ServiceCreationActivity;
import com.vendorprovider.ViewServicesActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kprogresshud.KProgressHUD;
import services.Services;

public class CategoryFragment extends Fragment {
    public static CategoryFragment newInstance() {
        CategoryFragment fragment = new CategoryFragment();
        return fragment;
    }

    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private KProgressHUD hud;
    RequestQueue requestQueue;
    public static JSONArray finalData;
    SharedPreferences preferences;
    Button btnNext;
    RelativeLayout mainCategoryLayout, subCategoryLayout;
    public static TextView txtCategoryName;
    public static TextView txtSubCategoryName;
    String checkEditMode;
    int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_details_fragment, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Category_ID", "");
        editor.putString("Category_Title", "");
        editor.commit();

        checkEditMode = preferences.getString("EditService", "");
        position = preferences.getInt("Position",0);

        cd = new ConnectionDetector(getContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            GetMainCategory();
        } else {
            Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_LONG).show();
        }

        mainCategoryLayout = (RelativeLayout) view.findViewById(R.id.mainCategoryLayout);
        subCategoryLayout = (RelativeLayout) view.findViewById(R.id.subCategoryLayout);
        txtCategoryName = (TextView) view.findViewById(R.id.txtCategoryName);
        txtSubCategoryName = (TextView) view.findViewById(R.id.txtSubCategoryName);
        btnNext = (Button) view.findViewById(R.id.btnNext);

        if (checkEditMode.equals("Edit")) {
            txtCategoryName.setText(ViewServicesActivity.arrayList.get(position).getParentCatName());
            txtSubCategoryName.setText(ViewServicesActivity.arrayList.get(position).getSubCatName());
        }
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String categoryName = txtCategoryName.getText().toString();
                String subCategoryName = txtSubCategoryName.getText().toString();

                if (categoryName.equals("")) {
                    Toast.makeText(getContext(), "Please Select Category!", Toast.LENGTH_SHORT).show();
                } else if (subCategoryName.equals("")) {
                    Toast.makeText(getContext(), "Please Select Sub-Category!", Toast.LENGTH_SHORT).show();
                } else {
                    ServiceCreationActivity.mViewPager.setCurrentItem(1, true);
                }
            }
        });
        mainCategoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Category", "Main");
                editor.commit();
                Intent mainCategory = new Intent(getContext(), CategoriesActivity.class);
                mainCategory.putExtra("Category", "Main");
                startActivity(mainCategory);
            }
        });
        subCategoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                String check = preferences.getString("Category_ID", "");
                if (check.equals("")) {
                    Toast.makeText(getContext(), "Please Select First Main Category!", Toast.LENGTH_LONG).show();
                } else {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("Category", "Sub");
                    editor.commit();
                    Intent subCategory = new Intent(getContext(), CategoriesActivity.class);
                    subCategory.putExtra("Category", "Sub");
                    startActivity(subCategory);
                }
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_more).setVisible(true);
        menu.findItem(R.id.action_logout).setVisible(false);
    }

    private void GetMainCategory() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.CATEGORY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response.toString());
                            String status = jObj.getString("status");
                            if (status.equals("success")) {
                                finalData = new JSONArray(jObj.optString("category"));
                            } else {
                                Toast.makeText(getContext(), "Data Not Found!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", "");
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(getContext());
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }
}