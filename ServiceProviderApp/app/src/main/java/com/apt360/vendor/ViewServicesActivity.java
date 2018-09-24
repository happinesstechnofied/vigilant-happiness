package com.apt360.vendor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import adapter.GalleryAdapter;
import adapter.QueAnsAdapter;
import adapter.ReviewAdapter;
import model.Feature;
import model.GalleryImage;
import model.QueAnsHistory;
import model.ReviewRatting;
import model.ServicesData;
import util.AppConstants;
import util.ImageLoadTask;

public class ViewServicesActivity extends AppCompatActivity {

    private RelativeLayout vendorStatusLayout;
    RelativeLayout layoutGallery;
    private GridLayoutManager lLayout;
    RecyclerView galleryListView;
    GalleryAdapter rcAdapter;
    public TextView txtVendorStatus, txtDescriptionTitle, txtDescription, txtCategoryTitle, txtCateTitle, txtSubCateTitle, txtMRP, txtSellingPrice, txtSavePrice, txtFeatures;
    TextView txtVendorName, txtVendorApartment, txtVendorLocation, txtVendorNumber,txtVendorEmail,txtVendorAddress,txtVendorCity,txtVendorPincode;
    public SimpleDraweeView categoryBanner;
    CircularImageView imgCateImage, imgSubCateImage;
    RelativeLayout layoutVendorStatus;
    Button addService, editService;
    Intent intent;
    int position;
    JSONArray ServiceData;
    JSONArray RateingArray;
    public static ArrayList<ServicesData> arrayList = new ArrayList<>();
    public static ArrayList<GalleryImage> galleryImages = new ArrayList<>();
    public static ArrayList<ReviewRatting> reviewRateList = new ArrayList<>();
    public static ArrayList<Feature> features = null;
    SharedPreferences preferences;
    RecyclerView ratingListView;
    ReviewAdapter reviewAdapter;
    RelativeLayout layoutRating;
    Button btnMoreReview;

    public static ArrayList<QueAnsHistory> queAnsList = new ArrayList<>();
    RecyclerView queansListView;
    RelativeLayout layoutQueAns;
    QueAnsAdapter queansAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_service_activity);
        initView();

        preferences = PreferenceManager.getDefaultSharedPreferences(ViewServicesActivity.this);
        intent = getIntent();
        position = intent.getIntExtra("position", 0);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("EditService", "Add");
                editor.commit();


                startActivity(new Intent(ViewServicesActivity.this, ServiceCreationActivity.class));
            }
        });
        editService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("EditService", "Edit");
                editor.commit();
                startActivity(new Intent(ViewServicesActivity.this, ServiceCreationActivity.class));
            }
        });

        btnMoreReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("Review", "AllReview");
                    editor.commit();
                    Intent review=new Intent(ViewServicesActivity.this,ReviewActivity.class);
                    startActivity(review);
            }
        });
        viewService();

    }



    private void viewService() {
        try {
            ServiceData = MainActivity.finalData;
            TypeReference<ArrayList<ServicesData>> typeRef = new TypeReference<ArrayList<ServicesData>>() {
            };

            arrayList = AppConstants.getMapper().readValue(ServiceData.toString(), typeRef);

            ServicesData nData = arrayList.get(position);

            txtCategoryTitle.setText(nData.getTitle());
            getSupportActionBar().setTitle(nData.getTitle());
            if (nData.getImage() == null || nData.getImage().equals("")) {
                categoryBanner.setImageResource(R.drawable.ic_placeholder);
            } else {
                Uri uri = Uri.parse(nData.getImage());
                categoryBanner.setImageURI(uri);
            }

            if (nData.getServiceStatus().equalsIgnoreCase("publish")) {
                layoutVendorStatus.setVisibility(View.INVISIBLE);
            } else {
                txtVendorStatus.setText(nData.getServiceStatus());
            }
            txtDescription.setText(Html.fromHtml(nData.getSortDescription()));
            txtCateTitle.setText(nData.getParentCatName());
            txtSubCateTitle.setText(nData.getSubCatName());

            txtMRP.setText("₹" + nData.getMaximumRetailPrice());
            txtSellingPrice.setText("₹" + nData.getSalePrice());
            int saveAmount = Integer.valueOf(nData.getMaximumRetailPrice()) - Integer.valueOf(nData.getSalePrice());
            txtSavePrice.setText("You Save : ₹" + String.valueOf(saveAmount));

            if (!nData.getParentImageSmall().equalsIgnoreCase("")) {
                URL url = new URL(nData.getParentImageSmall());
                new ImageLoadTask(url, imgCateImage).execute();
            }

            if (!nData.getSubImageSmall().equalsIgnoreCase("")) {
                URL url = new URL(nData.getParentImageSmall());
                new ImageLoadTask(url, imgSubCateImage).execute();
            }

            features = (ArrayList<Feature>) nData.getFeatures();

            txtFeatures.setText("");
            for (int i = 0; i < features.size(); i++) {
                txtFeatures.append("\u2022 " + features.get(i).getFeature().toString() + "\n\n");
            }
            galleryImages = (ArrayList<GalleryImage>) nData.getGalleryImages();
            if (galleryImages.size() == 0) {
                layoutGallery.setVisibility(View.GONE);
            } else {
                rcAdapter = new GalleryAdapter(ViewServicesActivity.this, galleryImages);
                galleryListView.setAdapter(rcAdapter);
            }

            /*customer review part*/
            reviewRateList = (ArrayList<ReviewRatting>) nData.getReviewRattings();
            if(reviewRateList.size()<5){
                btnMoreReview.setVisibility(View.GONE);
            }

            if (reviewRateList.size() == 0){
                layoutRating.setVisibility(View.GONE);
            }else{
                reviewAdapter = new ReviewAdapter(ViewServicesActivity.this, reviewRateList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                ratingListView.setLayoutManager(mLayoutManager);
                ratingListView.setItemAnimator(new DefaultItemAnimator());
                ratingListView.setAdapter(reviewAdapter);
            }

            /*customer question and answer*/
            queAnsList = (ArrayList<QueAnsHistory>) nData.getQueAnsHistory();
            if(queAnsList.size()==0){
                layoutQueAns.setVisibility(View.GONE);
            }else{
                queansAdapter = new QueAnsAdapter(ViewServicesActivity.this, queAnsList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                queansListView.setLayoutManager(mLayoutManager);
                queansListView.setItemAnimator(new DefaultItemAnimator());
                queansListView.setAdapter(queansAdapter);
            }

            txtVendorName.setText("Name: "+nData.getContactName());
            txtVendorApartment.setText("Apartment: "+nData.getAppartment());
            txtVendorLocation.setText("Location: "+nData.getLatitude()+"/"+nData.getLongitude());
            txtVendorNumber.setText("Mobile No: "+nData.getMobileNo());
            txtVendorEmail.setText("Email: "+nData.getEmail());
            txtVendorAddress.setText("Address: "+nData.getAddress());
            txtVendorCity.setText("City: "+nData.getCity());
            txtVendorPincode.setText("Pincode: "+nData.getPincode());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        layoutQueAns = (RelativeLayout) findViewById(R.id.layoutQueAns);
        queansListView = (RecyclerView) findViewById(R.id.queansListView);
        btnMoreReview = (Button) findViewById(R.id.btnMoreReview);
        layoutRating = (RelativeLayout) findViewById(R.id.layoutRating);
        ratingListView = (RecyclerView) findViewById(R.id.ratingListView);
        layoutGallery = (RelativeLayout) findViewById(R.id.layoutGallery);
        galleryListView = (RecyclerView) findViewById(R.id.galleryListView);
        lLayout = new GridLayoutManager(ViewServicesActivity.this, 3);
        galleryListView.setHasFixedSize(true);
        galleryListView.setLayoutManager(lLayout);
        layoutVendorStatus = (RelativeLayout) findViewById(R.id.layoutVendorStatus);
        categoryBanner = (SimpleDraweeView) findViewById(R.id.categoryBanner);
        txtCategoryTitle = (TextView) findViewById(R.id.txtCategoryTitle);
        txtDescriptionTitle = (TextView) findViewById(R.id.txtDescriptionTitle);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtVendorStatus = (TextView) findViewById(R.id.txtVendorStatus);
        txtCategoryTitle = (TextView) findViewById(R.id.txtCategoryTitle);
        txtCateTitle = (TextView) findViewById(R.id.txtCateTitle);
        txtSubCateTitle = (TextView) findViewById(R.id.txtSubCateTitle);
        txtMRP = (TextView) findViewById(R.id.txtMRP);
        txtSellingPrice = (TextView) findViewById(R.id.txtSellingPrice);
        txtSavePrice = (TextView) findViewById(R.id.txtSavePrice);
        txtFeatures = (TextView) findViewById(R.id.txtFeatures);
        imgCateImage = (CircularImageView) findViewById(R.id.imgCateImage);
        imgSubCateImage = (CircularImageView) findViewById(R.id.imgSubCateImage);
        txtVendorName = (TextView) findViewById(R.id.txtVendorName);
        txtVendorApartment = (TextView) findViewById(R.id.txtVendorApartment);
        txtVendorLocation = (TextView) findViewById(R.id.txtVendorLocation);
        txtVendorNumber = (TextView) findViewById(R.id.txtVendorNumber);
        txtVendorEmail = (TextView) findViewById(R.id.txtVendorEmail);
        txtVendorAddress = (TextView) findViewById(R.id.txtVendorAddress);
        txtVendorCity = (TextView) findViewById(R.id.txtVendorCity);
        txtVendorPincode = (TextView) findViewById(R.id.txtVendorPincode);
        addService = (Button) findViewById(R.id.addService);
        editService = (Button) findViewById(R.id.editService);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}