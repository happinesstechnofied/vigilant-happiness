package com.vendorprovider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import fragment.ContactFragment;
import fragment.FeatureFragment;
import fragment.AdditionalFragment;
import fragment.SearchingKeywordFragment;
import fragment.SuccessfullySubmitFragment;
import location.GPSTracker;
import util.AppConstants;
import util.NonSwipeableViewPager;
import fragment.CategoryFragment;
import fragment.ServiceFragment;
import stepper.StepperIndicator;

public class ServiceCreationActivity extends AppCompatActivity{


    private SectionsPagerAdapter mSectionsPagerAdapter;
    public static NonSwipeableViewPager mViewPager;
    private StepperIndicator stepperIndicator;
    SharedPreferences preferences;

    FeatureFragment abc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_creation_activity);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Service Creation");

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (NonSwipeableViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        stepperIndicator = (StepperIndicator)findViewById(R.id.stepperIndicator);

        stepperIndicator.showLabels(false);
        stepperIndicator.setViewPager(mViewPager);

        stepperIndicator.setViewPager(mViewPager, mViewPager.getAdapter().getCount() - 1); //
        stepperIndicator.setStepCount(6);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return CategoryFragment.newInstance();
                case 1:
                    return ServiceFragment.newInstance();
                case 2:
                    return AdditionalFragment.newInstance();
                case 3:
                    return FeatureFragment.newInstance();
                case 4:
                    return SearchingKeywordFragment.newInstance();
                case 5:
                    return ContactFragment.newInstance();
                case 6:
                    return SuccessfullySubmitFragment.newInstance();

            }
            return null;
        }

        @Override
        public int getCount() {
            // Show total pages.
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "CategoryFragment";
                case 1:
                    return "ServiceFragment";
                case 2:
                    return "AdditionalFragment";
                case 3:
                    return "FeatureFragment";
                case 4:
                    return "FeatureFragment";
                case 5:
                    return "ContactFragment";
                case 6:
                    return "SuccessFragment";
            }
            return null;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onAddField(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.feature_add_layout, null);
        rowView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, AppConstants.dpToPx(50,this)));
        FeatureFragment.newInstance().parent_linear_layout.addView(rowView, FeatureFragment.newInstance().parent_linear_layout.getChildCount() - 1);
    }
    public void onDelete(View v) {
        FeatureFragment.newInstance().parent_linear_layout.removeView((View) v.getParent());
    }

}