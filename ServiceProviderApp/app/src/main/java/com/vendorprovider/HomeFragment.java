package com.vendorprovider;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.connection.ConnectionDetector;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import adapter.HomeFragmentAdapter;
import kprogresshud.KProgressHUD;
import model.ServicesData;
import services.Services;
import util.AppConstants;
import util.RecyclerItemClickListener;

public class HomeFragment extends Fragment {

    ImageView imgContact;
    TextView txtVendorNumber;
    private static final int READ_STORAGE_CODE = 1001;
    RecyclerView recyclerView;
    /* flag for Internet connection status*/
    Boolean isInternetPresent = false;
    /*Connection detector class*/
    ConnectionDetector cd;
    private KProgressHUD hud;
    RequestQueue requestQueue;
    ArrayList<ServicesData> arrayList = new ArrayList<>();
    HomeFragmentAdapter adapter;
    SharedPreferences preferences;
    JSONArray finalData;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        cd = new ConnectionDetector(getContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            GetServiceData();
        } else {
            Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("Position", position);
                        editor.commit();
                    }
                })
        );
        return view;
    }

    private void GetServiceData() {

        try {
            finalData = MainActivity.finalData;
            TypeReference<ArrayList<ServicesData>> typeRef = new TypeReference<ArrayList<ServicesData>>() {
            };
            arrayList = AppConstants.getMapper().readValue(finalData.toString(), typeRef);
            adapter = new HomeFragmentAdapter(getContext(), arrayList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_more).setVisible(true);
        menu.findItem(R.id.action_logout).setVisible(false);
    }
}