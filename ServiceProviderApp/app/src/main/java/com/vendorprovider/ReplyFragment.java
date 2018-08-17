package com.vendorprovider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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
import adapter.QueAnsAdapter;
import adapter.ReplyAdapter;
import kprogresshud.KProgressHUD;
import model.QueAnsHistory;
import model.ServicesData;
import services.Services;
import util.AppConstants;
import util.RecyclerItemClickListener;

public class ReplyFragment extends Fragment {
    public static ReplyFragment newInstance() {
        ReplyFragment fragment = new ReplyFragment();
        return fragment;
    }

    Boolean isInternetPresent = false;
    /*Connection detector class*/
    ConnectionDetector cd;
    RecyclerView questionListView;
    JSONArray finalData;
    public static ArrayList<QueAnsHistory> arrayList= new ArrayList<>();
    private KProgressHUD hud;
    RequestQueue requestQueue;
    SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reply_fragment, container, false);

        cd = new ConnectionDetector(getContext());
        isInternetPresent = cd.isConnectingToInternet();

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        questionListView = (RecyclerView) view.findViewById(R.id.questionListView);
        questionListView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("Position", position);
                        editor.commit();
                        Intent i=new Intent(getContext(),QuestionReplyActivity.class);
                        startActivity(i);
                    }
                })
        );

        if (isInternetPresent) {
            hud = KProgressHUD.create(getContext())
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait");
            hud.show();
            GetQueAnsData();
        } else {
            Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void GetQueAnsData() {

            StringRequest postRequest = new StringRequest(Request.Method.POST, Services.QUE_ANS_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (hud.isShowing())
                                hud.dismiss();

                            try {

                                JSONObject obj = new JSONObject(response);
                                String status = obj.getString("status");
                                if (status.matches("success")) {
                                    JSONArray jsonArray = obj.getJSONArray("que_ans_history");
                                    TypeReference<ArrayList<QueAnsHistory>> typeRef = new TypeReference<ArrayList<QueAnsHistory>>() {
                                    };

                                    try {
                                        arrayList = AppConstants.getMapper().readValue(jsonArray.toString(), typeRef);
                                        ReplyAdapter queansAnsAdapter = new ReplyAdapter(getContext(), arrayList);
                                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                        questionListView.setLayoutManager(mLayoutManager);
                                        questionListView.setItemAnimator(new DefaultItemAnimator());
                                        questionListView.setAdapter(queansAnsAdapter);

                                    } catch (JsonParseException e) {
                                        e.printStackTrace();
                                    } catch (JsonMappingException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    if (hud.isShowing())
                                        hud.dismiss();

                                    String failed = obj.getString("message");
                                    Toast.makeText(getContext(), "Server Problem! Please try again.", Toast.LENGTH_SHORT).show();
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
                            if (hud.isShowing())
                                hud.dismiss();
                            Log.d("Error.Response", error.toString());
                            Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Service", "get_ques_history_sp");
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
                    return params;
                }
            };
            requestQueue = Volley.newRequestQueue(getContext());
            postRequest.setShouldCache(false);
            requestQueue.add(postRequest);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_more).setVisible(true);
        menu.findItem(R.id.action_logout).setVisible(false);
    }
}