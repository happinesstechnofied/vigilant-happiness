package com.vendorprovider;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import fragment.ContactFragment;
import location.GPSTracker;

public class MapLocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, MapWrapperLayout.OnDragListener {

    private GoogleApiClient mGoogleApiClient;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 11;
    private String TAG = "place";
    Geocoder geocoder;
    List<Address> addresses;
    GoogleMap mGoogleMap;
    Circle myCircle;
    String address, city, state, country, postalCode;
    ImageView btnMyLocation;
    GPSTracker gps;
    double latitude;
    double longitude;
    private int centerX = -1;
    private int centerY = -1;
    int index = 1;
    Button btnSubmitMapAddress;
    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        setContentView(R.layout.user_location_map_activity);

        gps = new GPSTracker(MapLocationActivity.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
        }

        btnMyLocation = (ImageView) findViewById(R.id.btnMyLocation);
        btnSubmitMapAddress = (Button) findViewById(R.id.btnSubmitMapAddress);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Your Location");

        geocoder = new Geocoder(this, Locale.getDefault());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleMap.clear();
                LatLng sydney = new LatLng(latitude, longitude);
                mGoogleMap.addMarker(new MarkerOptions().position(sydney).title(address).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_selected_location_marker)).draggable(true));
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18.0f));

                // draw circle on current location around pin.
                CircleOptions circleOptions = new CircleOptions()
                        .center(sydney)   //set center
                        .radius(300)   //set radius in meters
                        .fillColor(Color.parseColor("#801e8dea"))  //semi-transparent
                        .strokeColor(Color.parseColor("#1e8dea"))
                        .strokeWidth(1);
                myCircle = mGoogleMap.addCircle(circleOptions);
            }
        });

        //initialize google client api
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        //initialize auto complete place
        autocompletePlace();

        btnSubmitMapAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(MapLocationActivity.this);
                dialog.setContentView(R.layout.map_address_dialog);

                TextView txtMapAddress = (TextView) dialog.findViewById(R.id.txtMapAddress);
                Button btnSubmit = (Button) dialog.findViewById(R.id.btnSubmit);
                ImageButton btnClose = (ImageButton) dialog.findViewById(R.id.btnClose);

                txtMapAddress.setText(address);

                dialog.show();
                dialog.setCancelable(false);

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void autocompletePlace() {

        //create object of PlaceAutocompleteFragment.
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        //add listener to PlaceAutocompleteFragment.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            //Method will be auto call on selection of any place.
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.

                LatLng latLng = place.getLatLng();
                String mStringLatitude = String.valueOf(latLng.latitude);
                String mStringLongitude = String.valueOf(latLng.longitude);

                ContactFragment.latitude_location = String.valueOf(mStringLatitude);
                ContactFragment.longitude_location = String.valueOf(mStringLongitude);


                // set current location on map!
                try {
                    addresses = geocoder.getFromLocation(Double.parseDouble(mStringLatitude), Double.parseDouble(mStringLongitude), 5);

                    index = 1;
                    if (addresses.size() == 1) {
                        index = 0;
                    }

                    address = "";
                    if (addresses.get(index).getFeatureName() != null) {
                        address = addresses.get(index).getFeatureName();
                    }

                    if (addresses.get(index).getSubLocality() != null) {
                        if (address.equalsIgnoreCase("")) {
                            address = addresses.get(index).getSubLocality();
                        } else {
                            address = address + ", " + addresses.get(index).getSubLocality();
                        }
                    }

                    if (addresses.get(index).getThoroughfare() != null) {
                        if (address.equalsIgnoreCase("")) {
                            address = addresses.get(index).getThoroughfare();
                        } else {
                            address = address + ", " + addresses.get(index).getThoroughfare();
                        }
                    }

                    city = addresses.get(index).getLocality();
                    state = addresses.get(index).getAdminArea();
                    country = addresses.get(index).getCountryName();
                    postalCode = addresses.get(index).getPostalCode();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                ContactFragment.txtVendorAddress.setText(address);
                ContactFragment.txtVendorCity.setText(city);
                ContactFragment.txtVendorPincode.setText(postalCode);

                LatLng sydney = new LatLng(Double.parseDouble(mStringLatitude), Double.parseDouble(mStringLongitude));
                mGoogleMap.addMarker(new MarkerOptions().position(sydney).title(address).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_selected_location_marker)).draggable(true));
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18.0f));

            }

            //Method will be auto call, if error occur on selection of any place.
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mGoogleMap = googleMap;
        // set pin current location on map!
        LatLng sydney = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(sydney).title(address).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_selected_location_marker)).draggable(true));
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18.0f));

        ContactFragment.latitude_location = String.valueOf(latitude);
        ContactFragment.longitude_location = String.valueOf(longitude);

        // set current location on map!
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 5);
            index = 1;
            if (addresses.size() == 1) {
                index = 0;
            }

            address = "";
            if (addresses.get(index).getFeatureName() != null) {
                address = addresses.get(index).getFeatureName();
            }

            if (addresses.get(index).getSubLocality() != null) {
                if (address.equalsIgnoreCase("")) {
                    address = addresses.get(index).getSubLocality();
                } else {
                    address = address + ", " + addresses.get(index).getSubLocality();
                }
            }

            if (addresses.get(index).getThoroughfare() != null) {
                if (address.equalsIgnoreCase("")) {
                    address = addresses.get(index).getThoroughfare();
                } else {
                    address = address + ", " + addresses.get(index).getThoroughfare();
                }
            }

            city = addresses.get(index).getLocality();
            state = addresses.get(index).getAdminArea();
            country = addresses.get(index).getCountryName();
            postalCode = addresses.get(index).getPostalCode();

        } catch (IOException e) {
            e.printStackTrace();
        }

        ContactFragment.txtVendorAddress.setText(address);
        ContactFragment.txtVendorCity.setText(city);
        ContactFragment.txtVendorPincode.setText(postalCode);

        // draw circle on current location around pin.
        CircleOptions circleOptions = new CircleOptions()
                .center(sydney)   //set center
                .radius(300)   //set radius in meters
                .fillColor(Color.parseColor("#801e8dea"))  //semi-transparent
                .strokeColor(Color.parseColor("#1e8dea"))
                .strokeWidth(1);
        myCircle = googleMap.addCircle(circleOptions);

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                Double lat = marker.getPosition().latitude;
                Double lang = marker.getPosition().longitude;

                ContactFragment.latitude_location = String.valueOf(lat);
                ContactFragment.longitude_location = String.valueOf(lang);

                // set current location on map!
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 5);
                    index = 1;
                    if (addresses.size() == 1) {
                        index = 0;
                    }

                    address = "";
                    if (addresses.get(index).getFeatureName() != null) {
                        address = addresses.get(index).getFeatureName();
                    }

                    if (addresses.get(index).getSubLocality() != null) {
                        if (address.equalsIgnoreCase("")) {
                            address = addresses.get(index).getSubLocality();
                        } else {
                            address = address + ", " + addresses.get(index).getSubLocality();
                        }
                    }

                    if (addresses.get(index).getThoroughfare() != null) {
                        if (address.equalsIgnoreCase("")) {
                            address = addresses.get(index).getThoroughfare();
                        } else {
                            address = address + ", " + addresses.get(index).getThoroughfare();
                        }
                    }

                    city = addresses.get(index).getLocality();
                    state = addresses.get(index).getAdminArea();
                    country = addresses.get(index).getCountryName();
                    postalCode = addresses.get(index).getPostalCode();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                ContactFragment.txtVendorAddress.setText(address);
                ContactFragment.txtVendorCity.setText(city);
                ContactFragment.txtVendorPincode.setText(postalCode);

                marker.setSnippet(String.valueOf(lat));
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }
        });
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDrag(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            Projection projection = (mGoogleMap != null && mGoogleMap
                    .getProjection() != null) ? mGoogleMap.getProjection()
                    : null;
            //
            if (projection != null) {
                LatLng centerLatLng = projection.fromScreenLocation(new Point(
                        centerX, centerY));
                //  updateLocation(centerLatLng);
            }
        }
    }
}