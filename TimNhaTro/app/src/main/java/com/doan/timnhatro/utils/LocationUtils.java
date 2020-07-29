package com.doan.timnhatro.utils;

import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.doan.timnhatro.base.BaseApplication;
import com.doan.timnhatro.callback.OnDirectionListener;
import com.doan.timnhatro.model.DirectionsResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;

public class LocationUtils {

    private static LocationUtils locationUtils;

    private LatLng latLng;

    public static LocationUtils getInstance(){
        if (locationUtils == null){
            locationUtils = new LocationUtils();
        }
        return locationUtils;
    }

    private LocationUtils() {
        buildLocationAPI();
    }

    private void buildLocationAPI() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(5000)
                .setInterval(10000);
        FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(BaseApplication.getContext());
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback , Looper.myLooper());
    }

    private LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            latLng = new LatLng(locationResult.getLastLocation().getLatitude(),
                    locationResult.getLastLocation().getLongitude());
        }
    };

    public boolean isGPSEnable(){
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(BaseApplication.getContext().getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        }else{
            locationProviders = Settings.Secure.getString(BaseApplication.getContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public LatLng getLocation(){
        if (latLng == null){
            latLng = new LatLng(0,0);
        }
        return latLng;
    }

    private static final String url = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String key = "AIzaSyAoDvmEF49FQVfuh1O1ZyNUubq_C4ZSIjI";
    private static final String bike = "driving";

    public void getDirection(LatLng orgin, final OnDirectionListener directionListener){

        RequestQueue queue = Volley.newRequestQueue(BaseApplication.getContext());
        String urlBuilder = url +
                "origin=" + orgin.latitude + "," + orgin.longitude +
                "&destination=" + getLocation().latitude + "," + getLocation().longitude +
                "&mode=" + bike +
                "&key=" + key;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlBuilder,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        directionListener.onSuccess(new Gson().fromJson(response, DirectionsResult.class));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                directionListener.onError();
            }
        });
        queue.add(stringRequest);
    }

    public ArrayList<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        ArrayList<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
}
