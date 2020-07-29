package com.doan.timnhatro.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OverviewPolyline implements Parcelable {

    @SerializedName("points")
    @Expose
    private String points;

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.points);
    }

    public OverviewPolyline() {
    }

    protected OverviewPolyline(Parcel in) {
        this.points = in.readString();
    }

    public static final Creator<OverviewPolyline> CREATOR = new Creator<OverviewPolyline>() {
        @Override
        public OverviewPolyline createFromParcel(Parcel source) {
            return new OverviewPolyline(source);
        }

        @Override
        public OverviewPolyline[] newArray(int size) {
            return new OverviewPolyline[size];
        }
    };
}
