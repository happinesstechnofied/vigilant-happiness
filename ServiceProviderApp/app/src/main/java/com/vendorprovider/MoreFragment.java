package com.vendorprovider;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MoreFragment extends Fragment {
    public static MoreFragment newInstance() {
        MoreFragment fragment = new MoreFragment();
        return fragment;
    }

    RelativeLayout layoutShareApp, layoutDownloadApp, layoutRateUs, layoutAbout, layoutPolicy, layoutTerm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.more_fragment, container, false);

        layoutShareApp = (RelativeLayout) view.findViewById(R.id.layoutShareApp);
        layoutDownloadApp = (RelativeLayout) view.findViewById(R.id.layoutDownloadApp);
        layoutRateUs = (RelativeLayout) view.findViewById(R.id.layoutRateUs);
        layoutAbout = (RelativeLayout) view.findViewById(R.id.layoutAbout);
        layoutPolicy = (RelativeLayout) view.findViewById(R.id.layoutPolicy);
        layoutTerm = (RelativeLayout) view.findViewById(R.id.layoutTerm);

        layoutAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"About Us",Toast.LENGTH_SHORT).show();
            }
        });
        layoutPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Privacy Policy",Toast.LENGTH_SHORT).show();
            }
        });
        layoutTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Term and Condition",Toast.LENGTH_SHORT).show();
            }
        });
        layoutShareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = getContext();
                int applicationNameId = context.getApplicationInfo().labelRes;
                final String appPackageName = context.getPackageName();
                Intent shareI = new Intent(Intent.ACTION_SEND);
                shareI.setType("text/plain");
                shareI.putExtra(Intent.EXTRA_SUBJECT, getActivity().getString(applicationNameId));
                String text = "Vendor Services Provider!";
                String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
                shareI.putExtra(Intent.EXTRA_TEXT, text + " " + link);
                startActivity(Intent.createChooser(shareI, "Share App:"));
            }
        });
        layoutDownloadApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = getContext();
                Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
                }
            }
        });
        layoutRateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = getContext();
                Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
                }
            }
        });
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_more).setVisible(false);
        menu.findItem(R.id.action_logout).setVisible(false);
    }
}