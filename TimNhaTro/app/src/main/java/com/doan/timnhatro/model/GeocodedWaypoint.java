package com.doan.timnhatro.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeocodedWaypoint implements Parcelable {

    @SerializedName("geocoder_status")
    @Expose
    private String geocoderStatus;
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("types")
    @Expose
    private List<String> types = null;

    public String getGeocoderStatus() {
        return geocoderStatus;
    }

    public void setGeocoderStatus(String geocoderStatus) {
        this.geocoderStatus = geocoderStatus;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.geocoderStatus);
        dest.writeString(this.placeId);
        dest.writeStringList(this.types);
    }

    public GeocodedWaypoint() {
    }

    protected GeocodedWaypoint(Parcel in) {
        this.geocoderStatus = in.readString();
        this.placeId = in.readString();
        this.types = in.createStringArrayList();
    }

    public static final Creator<GeocodedWaypoint> CREATOR = new Creator<GeocodedWaypoint>() {
        @Override
        public GeocodedWaypoint createFromParcel(Parcel source) {
            return new GeocodedWaypoint(source);
        }

        @Override
        public GeocodedWaypoint[] newArray(int size) {
            return new GeocodedWaypoint[size];
        }
    };
}