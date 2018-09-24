package com.apt360.vendor;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.connection.ConnectionDetector;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;

import adapter.HomeFragmentAdapter;
import model.ServicesData;
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