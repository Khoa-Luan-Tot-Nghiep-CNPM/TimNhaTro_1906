package com.doan.timnhatro.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.doan.timnhatro.R;
import com.doan.timnhatro.base.Constants;
import com.doan.timnhatro.callback.OnDirectionListener;
import com.doan.timnhatro.model.DirectionsResult;
import com.doan.timnhatro.model.EndLocation;
import com.doan.timnhatro.model.Northeast;
import com.doan.timnhatro.model.Position;
import com.doan.timnhatro.model.Southwest;
import com.doan.timnhatro.model.StartLocation;
import com.doan.timnhatro.utils.LocationUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.Objects;

public class DirectActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Position position;
    private GoogleMap googleMap;
    private DirectionsResult directions;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct);
        
        addMaps();
        getIntentData();
    }

    private void addMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMaps);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
    }

    private void getIntentData() {
        position = getIntent().getParcelableExtra(Constants.POSITION);
    }

    public void cnClickCancel(View view) {
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        addMapStyle();
        addLineArress();
    }

    private void addLineArress() {
        LocationUtils.getInstance().getDirection(new LatLng(position.getLatitude(), position.getLongitude()),
                new OnDirectionListener() {
                    @Override
                    public void onSuccess(DirectionsResult direction) {
                        if (direction.getRoutes().size() > 0) {
                            directions = direction;
                            addMarker();
                            addPolyline();
                            updateCamera();
                        }else {
                            Toast.makeText(getApplicationContext(), "Không thể tìm thấy đoạn đường", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onError() {
                        Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi khi tìm kiếm đoạn đường", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addMarker() {
        StartLocation startLocation   = directions.getRoutes().get(0).getLegs().get(0).getStartLocation();
        EndLocation endLocation     = directions.getRoutes().get(0).getLegs().get(0).getEndLocation();

        LatLng start    = new LatLng(startLocation.getLat(),startLocation.getLng());
        LatLng end      = new LatLng(endLocation.getLat(),endLocation.getLng());

        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_des))
                .title(directions.getRoutes().get(0).getLegs().get(0).getStartAddress())
                .position(start));

        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_orgin))
                .title(directions.getRoutes().get(0).getLegs().get(0).getEndAddress())
                .position(end));
    }

    private void addMapStyle() {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
    }

    private void addPolyline() {
        PolylineOptions polylineOptions = new PolylineOptions()
                .geodesic(true)
                .jointType(JointType.ROUND)
                .color(Color.MAGENTA)
                .width(12)
                .startCap(new RoundCap())
                .endCap(new RoundCap());
        polylineOptions.addAll(LocationUtils.getInstance().decodePolyLine(directions.getRoutes()
                .get(0)
                .getOverviewPolyline().getPoints()));
        googleMap.addPolyline(polylineOptions);
    }

    private void updateCamera() {
        Northeast northeast = directions.getRoutes().get(0).getBounds().getNortheast();
        Southwest southwest = directions.getRoutes().get(0).getBounds().getSouthwest();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(northeast.getLat(),northeast.getLng()));
        builder.include(new LatLng(southwest.getLat(),southwest.getLng()));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),100));
    }
}
