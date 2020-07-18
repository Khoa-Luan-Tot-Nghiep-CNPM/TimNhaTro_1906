package com.doan.timnhatro.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DirectionsResult implements Parcelable {

    @SerializedName("geocoded_waypoints")
    @Expose
    private List<GeocodedWaypoint> geocodedWaypoints = null;
    @SerializedName("routes")
    @Expose
    private List<Route> routes = null;
    @SerializedName("status")
    @Expose
    private String status;

    public List<GeocodedWaypoint> getGeocodedWaypoints() {
        return geocodedWaypoints;
    }

    public void setGeocodedWaypoints(List<GeocodedWaypoint> geocodedWaypoints) {
        this.geocodedWaypoints = geocodedWaypoints;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.geocodedWaypoints);
        dest.writeList(this.routes);
        dest.writeString(this.status);
    }

    public DirectionsResult() {
    }

    protected DirectionsResult(Parcel in) {
        this.geocodedWaypoints = new ArrayList<GeocodedWaypoint>();
        in.readList(this.geocodedWaypoints, GeocodedWaypoint.class.getClassLoader());
        this.routes = new ArrayList<Route>();
        in.readList(this.routes, Route.class.getClassLoader());
        this.status = in.readString();
    }

    public static final Creator<DirectionsResult> CREATOR = new Creator<DirectionsResult>() {
        @Override
        public DirectionsResult createFromParcel(Parcel source) {
            return new DirectionsResult(source);
        }

        @Override
        public DirectionsResult[] newArray(int size) {
            return new DirectionsResult[size];
        }
    };
}
