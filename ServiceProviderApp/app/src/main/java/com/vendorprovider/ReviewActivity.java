package com.vendorprovider;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import adapter.ReviewAdapter;

/**
 * Created by rajgandhi on 10/08/18.
 */

public class ReviewActivity extends AppCompatActivity {


    RecyclerView ratingListView;
    ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_activity);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Customer Review");

        ratingListView = (RecyclerView) findViewById(R.id.ratingListView);

        reviewAdapter = new ReviewAdapter(ReviewActivity.this, ViewServicesActivity.reviewRateList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        ratingListView.setLayoutManager(mLayoutManager);
        ratingListView.setItemAnimator(new DefaultItemAnimator());
        ratingListView.setAdapter(reviewAdapter);

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
