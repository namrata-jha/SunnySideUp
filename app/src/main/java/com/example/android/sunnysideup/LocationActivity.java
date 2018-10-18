package com.example.android.sunnysideup;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

public class LocationActivity extends AppCompatActivity {

    TextView tv;

    private static final int AUTOCOMPLETE_LOCATION_ACTIVITY_REQUEST_CODE = 102;

    View parentView;

    private static int CHANGE_LOCATION;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    LocationPermissionUtils locationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        CHANGE_LOCATION = Objects.requireNonNull(getIntent().getExtras()).getInt("call_code");

        Objects.requireNonNull(getSupportActionBar()).hide();
        parentView = findViewById(android.R.id.content);
        tv = findViewById(R.id.text);
        locationUtils = new LocationPermissionUtils(LocationActivity.this, parentView);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == LocationPermissionUtils.REQUEST_LOCATION) {
            onFindLocationButtonClick(null);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


    }

    public void onLocationButtonClick(View view) {

        Intent intent = new Intent(this, PlaceAutocompleteActivity.class);
        startActivityForResult(intent, AUTOCOMPLETE_LOCATION_ACTIVITY_REQUEST_CODE);
    }

    public void onFindLocationButtonClick(View view) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            locationUtils.requestLocationPermissions();
        }
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double lat = location.getLatitude();
                                double lang = location.getLongitude();

                                String name = null;
                                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                                try {
                                    name = gcd.getFromLocation(lat, lang, 1).get(0).getLocality();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                                if(CHANGE_LOCATION == 1){
                                    SharedPreferences.Editor editor = MainActivity.settingsPref.edit();
                                    editor.putString("location", name);
                                    editor.apply();
                                    SharedPreferences sh = getSharedPreferences("MyLocationPreferences", MODE_PRIVATE);
                                    SharedPreferences.Editor shEditor = sh.edit();
                                    shEditor.putString("location", name);
                                    shEditor.putFloat("latitude", (float)lat);
                                    shEditor.putFloat("longitude", (float)lang);
                                    shEditor.apply();

                                }


                                Intent intent = new Intent();
                                Bundle extras = new Bundle();
                                extras.putDouble("LATITUDE", lat);
                                extras.putDouble("LONGITUDE", lang);
                                extras.putString("LOCATION", name);
                                intent.putExtra("LOCATION_BUNDLE", extras);

                                setResult(RESULT_OK, intent);
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();


                            } else {
                                Toast.makeText(LocationActivity.this, "Location not found!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_LOCATION_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                double lat, lang;
                Bundle intentBundle = data.getBundleExtra("LOCATION_BUNDLE");
                String display = intentBundle.getString("NAME") + " " + intentBundle.getDouble("LATITUDE") +
                        "," + intentBundle.getDouble("LONGITUDE");

                lat = intentBundle.getDouble("LATITUDE");
                lang = intentBundle.getDouble("LONGITUDE");

                tv.setText(display);

                if(CHANGE_LOCATION == 1) {
                    SharedPreferences.Editor editor = MainActivity.settingsPref.edit();
                    editor.putString("location", intentBundle.getString("NAME"));
                    editor.apply();
                    SharedPreferences sh = getSharedPreferences("MyLocationPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor shEditor = sh.edit();
                    shEditor.putString("location", intentBundle.getString("NAME"));
                    shEditor.putFloat("latitude", (float)lat);
                    shEditor.putFloat("longitude", (float)lang);
                    shEditor.apply();
                }

                Intent intent = new Intent();
                Bundle extras = new Bundle();
                extras.putDouble("LATITUDE", lat);
                extras.putDouble("LONGITUDE", lang);
                extras.putString("LOCATION", intentBundle.getString("NAME"));
                intent.putExtra("LOCATION_BUNDLE", extras);

                setResult(RESULT_OK, intent);
                finish();
                startActivity(new Intent(this, MainActivity.class));
            }
        }
    }





}

