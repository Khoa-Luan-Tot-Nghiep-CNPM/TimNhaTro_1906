package com.doan.timnhatro.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.doan.timnhatro.R;
import com.doan.timnhatro.base.Constants;
import com.doan.timnhatro.model.MotelRoom;
import com.doan.timnhatro.utils.LocationUtils;
import com.doan.timnhatro.view.DetailMotelRoomActivity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private View container;
    private GoogleMap googleMap;
    private Spinner snRadius;
    private LatLng myPosition = LocationUtils.getInstance().getLocation();

    public MapsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,Bundle savedInstanceState) {
        container = inflater.inflate(R.layout.fragment_maps, viewGroup, false);

        initView();
        addMaps();
        addEvents();

        return container;
    }

    private void initView() {
        snRadius = container.findViewById(R.id.snRadius);
    }

    private void addMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMaps);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
    }

    private void addEvents() {
        snRadius.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (googleMap != null)
                switch (position){
                    case 0:
                        addMyMarker(0.5,16);
                        break;
                    case 1:
                        addMyMarker(1,15);
                        break;
                    case 2:
                        addMyMarker(2,14);
                        break;
                    case 3:
                        addMyMarker(3, (float) 13.3);
                        break;
                    case 4:
                        addMyMarker(4,13);
                        break;
                    case 5:
                        addMyMarker(5, (float) 12.5);
                        break;
                    case 6:
                        addMyMarker(10, (float) 11.5);
                        break;
                    case 7:
                        addMyMarker(20, (float) 10.5);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void addMyMarker(double radius,float zoom) {
        googleMap.clear();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myPosition.latitude,myPosition.longitude),zoom));
        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location_32dp))
                .position(myPosition));

        CircleOptions circleOptions = new CircleOptions()
                .strokeWidth(5)
                .strokeColor(Color.GREEN)
                .fillColor(Color.parseColor("#5C57FF95"))
                .center(myPosition)
                .radius(radius * 1000);
        this.googleMap.addCircle(circleOptions);

        getAccommodationAround(radius);
    }

    private void getAccommodationAround(double radius) {
        DatabaseReference mapReference = FirebaseDatabase.getInstance().getReference().child("Maps");
        final GeoFire geoFire = new GeoFire(mapReference);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(myPosition.latitude, myPosition.longitude), radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, final GeoLocation location) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        googleMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_motel_room_32dp))
                                .position(new LatLng(location.latitude,location.longitude)))
                                .setTag(key);
                    }
                });
            }
            @Override
            public void onKeyExited(String key) {
            }
            @Override
            public void onKeyMoved(String key, GeoLocation location) {
            }
            @Override
            public void onGeoQueryReady() {
            }
            @Override
            public void onGeoQueryError(DatabaseError error) {
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        addMapStyle();
//        addMyMarker(0.5);

        this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (marker.getTag() == null){
                    return false;
                }

                FirebaseDatabase.getInstance().getReference()
                        .child("MotelRoom")
                        .child(marker.getTag().toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    MotelRoom motelRoom = dataSnapshot.getValue(MotelRoom.class);

                                    Intent intent = new Intent(getActivity(), DetailMotelRoomActivity.class);
                                    intent.putExtra(Constants.MOTEL_ROOM, motelRoom);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                return true;
            }
        });
    }

    private void addMapStyle() {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style));
    }
}
