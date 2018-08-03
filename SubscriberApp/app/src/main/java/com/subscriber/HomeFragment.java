package com.subscriber;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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
import com.facebook.drawee.view.SimpleDraweeView;
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

import adapter.CategoryAdapter;
import adapter.SearchServiceAdapter;
import adapter.ServiceAreaAdapter;
import adapter.SponsorAdapter;
import adapter.TopServicesAdapter;
import connection.ConnectionDetector;
import kprogresshud.KProgressHUD;
import model.BannerAdData;
import model.Categories;
import model.ServicesData;
import services.Services;
import util.AppConstants;
import util.AutoScrollViewPager;
import util.JazzyViewPager;
import util.OutlineContainer;

public class HomeFragment extends Fragment {

    private AutoScrollViewPager viewPager;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private KProgressHUD hud;
    RequestQueue requestQueue;
    ArrayList<BannerAdData> bannerAdList = new ArrayList<>();
    ArrayList<ServicesData> arrayList = new ArrayList<>();
    private RecyclerView search_list;
    private RecyclerView service_list;
    private RecyclerView top_service_list;
    SearchServiceAdapter adapter;
    ServiceAreaAdapter serviceAreaAdapter;
    TopServicesAdapter topServicesAdapter;
    private JazzyViewPager mViewPager;
    int currentPage;
    DetailOnPageChangeListener pageChange_Listener;
    FeatureServiceViewPager ad;
    RecyclerView recyclerView;
    private GridLayoutManager lLayout;
    CategoryAdapter rcAdapter;
    ArrayList<Categories> categoriesList = new ArrayList<>();

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

        /*creating connection detector class instance*/
        cd = new ConnectionDetector(getContext());
        isInternetPresent = cd.isConnectingToInternet();

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        lLayout = new GridLayoutManager(getContext(), 3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(lLayout);
        viewPager = (AutoScrollViewPager) view.findViewById(R.id.pager);
        viewPager.startAutoScroll();
        viewPager.setInterval(6000);
        viewPager.setCycle(true);
        viewPager.setStopScrollWhenTouch(true);
        search_list = (RecyclerView) view.findViewById(R.id.search_list);
        service_list = (RecyclerView) view.findViewById(R.id.service_list);
        top_service_list = (RecyclerView) view.findViewById(R.id.top_service_list);
        mViewPager = (JazzyViewPager) view.findViewById(R.id.featureViewPager);
        setUpViewPager(JazzyViewPager.TransitionEffect.CubeIn);
        if (isInternetPresent) {
            hud = KProgressHUD.create(getContext())
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait");
            hud.show();
            SponsorBanner();
            GetSearchServiceData();
            GetServiceAreaData();
            GetTopServiceData();
            GetFeatureServiceData();
            GetCategoryData();

        } else {
            Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void GetCategoryData() {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.CATEGORY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (hud.isShowing())
                            hud.dismiss();

                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("category");
                            TypeReference<ArrayList<Categories>> typeRef = new TypeReference<ArrayList<Categories>>() {
                            };

                            categoriesList = AppConstants.getMapper().readValue(jsonArray.toString(), typeRef);

                            rcAdapter = new CategoryAdapter(getContext(), categoriesList);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(rcAdapter);
                            hud.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (JsonParseException e) {

                        } catch (JsonMappingException e) {

                        } catch (IOException e) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                params.put("user_id", "");
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(getContext());
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }

    private void setUpViewPager(JazzyViewPager.TransitionEffect effect) {
        mViewPager.setTransitionEffect(effect);
        mViewPager.setPageMargin(15);
        mViewPager.setOffscreenPageLimit(3);
    }

    class DetailOnPageChangeListener extends JazzyViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            currentPage = position;
        }

        public int getCurrentPage() {
            return currentPage;
        }
    }

    private class FeatureServiceViewPager extends PagerAdapter {
        private LayoutInflater inflater;
        private ArrayList<ServicesData> pData;

        public FeatureServiceViewPager(Activity context, ArrayList<ServicesData> pData) {
            this.pData = pData;
            inflater = context.getLayoutInflater();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            final View viewpager = inflater.inflate(R.layout.custom_feature_service_fragment, null);
            SimpleDraweeView vendorBanner = (SimpleDraweeView) viewpager.findViewById(R.id.vendorBanner);
            ServicesData nData = pData.get(position);
            if (nData.getImage() == null && nData.getImage().equalsIgnoreCase("")) {
                vendorBanner.setImageResource(R.drawable.ic_placeholder);
            } else {
                Uri uri = Uri.parse(nData.getImage());
                vendorBanner.setImageURI(uri);
            }

            ((JazzyViewPager) container).addView(viewpager, 0);
            return viewpager;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object obj) {
            container.removeView(mViewPager.findViewFromObject(position));
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            if (view instanceof OutlineContainer) {
                return ((OutlineContainer) view).getChildAt(0) == obj;
            } else {
                return view == obj;
            }
        }
    }
    private void GetSearchServiceData() {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.GET_SERVICE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (hud.isShowing())
                            hud.dismiss();

                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("services");
                            TypeReference<ArrayList<ServicesData>> typeRef = new TypeReference<ArrayList<ServicesData>>() {
                            };

                            //   finalData = jsonArray;

                            arrayList = AppConstants.getMapper().readValue(jsonArray.toString(), typeRef);
                            //Collections.shuffle(arrayList);
                            adapter = new SearchServiceAdapter(getContext(),arrayList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                            search_list.setLayoutManager(mLayoutManager);
                            search_list.setItemAnimator(new DefaultItemAnimator());
                            search_list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                            search_list.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                        } catch (JsonMappingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
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
                params.put("Service", "get_subscriber_service_list");
                params.put("user_id", "16");
                /*if (preferences.getString("user_data", "") != null && !preferences.getString("user_data", "").equalsIgnoreCase("")) {
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
                }*/
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(getContext());
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }

    private void GetTopServiceData() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.GET_SERVICE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (hud.isShowing())
                            hud.dismiss();

                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("services");
                            TypeReference<ArrayList<ServicesData>> typeRef = new TypeReference<ArrayList<ServicesData>>() {
                            };

                            //   finalData = jsonArray;

                            arrayList = AppConstants.getMapper().readValue(jsonArray.toString(), typeRef);
                            //Collections.shuffle(arrayList);
                            topServicesAdapter = new TopServicesAdapter(getContext(),arrayList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                            top_service_list.setLayoutManager(mLayoutManager);
                            top_service_list.setItemAnimator(new DefaultItemAnimator());
                            top_service_list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                            top_service_list.setAdapter(topServicesAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                        } catch (JsonMappingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
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
                params.put("Service", "get_subscriber_service_list");
                params.put("user_id", "16");
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(getContext());
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }

    private void GetServiceAreaData() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.GET_SERVICE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (hud.isShowing())
                            hud.dismiss();

                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("services");
                            TypeReference<ArrayList<ServicesData>> typeRef = new TypeReference<ArrayList<ServicesData>>() {
                            };

                            //   finalData = jsonArray;

                            arrayList = AppConstants.getMapper().readValue(jsonArray.toString(), typeRef);
                            //Collections.shuffle(arrayList);
                            serviceAreaAdapter = new ServiceAreaAdapter(getContext(),arrayList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                            service_list.setLayoutManager(mLayoutManager);
                            service_list.setItemAnimator(new DefaultItemAnimator());
                            service_list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                            service_list.setAdapter(serviceAreaAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                        } catch (JsonMappingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
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
                params.put("Service", "get_subscriber_service_list");
                params.put("user_id", "16");
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(getContext());
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }

    private void GetFeatureServiceData() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, Services.GET_SERVICE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (hud.isShowing())
                            hud.dismiss();

                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("services");
                            TypeReference<ArrayList<ServicesData>> typeRef = new TypeReference<ArrayList<ServicesData>>() {
                            };

                            //   finalData = jsonArray;

                            arrayList = AppConstants.getMapper().readValue(jsonArray.toString(), typeRef);
                            //Collections.shuffle(arrayList);
                            ad = new FeatureServiceViewPager(getActivity(),arrayList);
                            mViewPager.setAdapter(ad);
                            pageChange_Listener = new DetailOnPageChangeListener();
                            mViewPager.setOnPageChangeListener(pageChange_Listener);
                            ad.notifyDataSetChanged();
                            mViewPager.setCurrentItem(currentPage);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                        } catch (JsonMappingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
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
                params.put("Service", "get_subscriber_service_list");
                params.put("user_id", "16");
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(getContext());
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }

    private void SponsorBanner() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Services.SPONSOR_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response.toString());
                            if (jObj.getString("status").equalsIgnoreCase("true")) {

                                TypeReference<ArrayList<BannerAdData>> typeRef = new TypeReference<ArrayList<BannerAdData>>() {
                                };
                                bannerAdList = AppConstants.getMapper().readValue(jObj.getString("banner").toString(), typeRef);
                                SponsorAdapter sponsorAdapter = new SponsorAdapter(getContext(), bannerAdList);
                                viewPager.setAdapter(sponsorAdapter);
                            } else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                        } catch (JsonMappingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (hud.isShowing())
                            hud.dismiss();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Service", "get_banners");
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_more).setVisible(false);
        menu.findItem(R.id.action_logout).setVisible(false);
    }
}