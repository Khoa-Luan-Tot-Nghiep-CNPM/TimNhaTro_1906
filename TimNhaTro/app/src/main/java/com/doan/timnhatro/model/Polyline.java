package com.doan.timnhatro.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Polyline implements Parcelable {

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

    public Polyline() {
    }

    protected Polyline(Parcel in) {
        this.points = in.readString();
    }

    public static final Creator<Polyline> CREATOR = new Creator<Polyline>() {
        @Override
        public Polyline createFromParcel(Parcel source) {
            return new Polyline(source);
        }

        @Override
        public Polyline[] newArray(int size) {
            return new Polyline[size];
        }
    };
}
