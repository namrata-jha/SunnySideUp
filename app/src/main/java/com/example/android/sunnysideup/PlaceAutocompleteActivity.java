package com.example.android.sunnysideup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.util.Objects;

public class PlaceAutocompleteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autocomplete);
        Objects.requireNonNull(getSupportActionBar()).hide();


        // Retrieve the PlaceAutocompleteFragment.
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Intent intent = new Intent();
                Bundle extras = new Bundle();
                extras.putString("NAME", place.getName().toString());
                extras.putDouble("LATITUDE", place.getLatLng().latitude);
                extras.putDouble("LONGITUDE", place.getLatLng().longitude);
                intent.putExtra("LOCATION_BUNDLE", extras);
                setResult(RESULT_OK, intent);
                finish();
            }


            @Override
            public void onError(Status status) {
                Toast.makeText(PlaceAutocompleteActivity.this, "Error: "+ status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
