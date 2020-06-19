package com.doan.timnhatro.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.doan.timnhatro.R;
import com.doan.timnhatro.base.Constants;
import com.doan.timnhatro.model.Position;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.Objects;

public class SelectLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private LatLng myLocationSelect;
    private GoogleMap googleMap;
    private Button btnSelectDone;
    private LinearLayout mapContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Places.initialize(getApplicationContext(), Constants.KEY_MAPS);
        setContentView(R.layout.activity_select_location);

        initView();
        addMaps();
        addAutoComplete();
    }

    private void initView() {
        mapContainer    = findViewById(R.id.mapContainer);
        btnSelectDone   = findViewById(R.id.btnSelectDone);
    }

    private void addMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMaps);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
    }

    private void addAutoComplete() {
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        if (autocompleteFragment == null){
            return;
        }
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mapContainer.setVisibility(View.VISIBLE);
                btnSelectDone.setVisibility(View.VISIBLE);

                myLocationSelect = place.getLatLng();

                addMarker();
            }
            @Override
            public void onError(@NonNull Status status) {
                if (status.getStatusMessage() == null){
                    return;
                }
                Log.d("dfgdfgdfgdf", status.getStatusMessage());
            }
        });
    }

    private void addMarker() {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocationSelect,17));
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location_32dp))
                .position(myLocationSelect));
    }

    public void viewSelectPosition(View view) {
        Position position = new Position();
        position.setLatitude(myLocationSelect.latitude);
        position.setLongitude(myLocationSelect.longitude);

        Intent intent = new Intent();
        intent.putExtra(Constants.POSITION,position);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
}
