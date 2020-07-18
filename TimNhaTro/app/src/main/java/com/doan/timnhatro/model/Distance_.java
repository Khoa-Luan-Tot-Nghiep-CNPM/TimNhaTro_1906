package com.doan.timnhatro.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Distance_ implements Parcelable {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("value")
    @Expose
    private Integer value;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
        dest.writeValue(this.value);
    }

    public Distance_() {
    }

    protected Distance_(Parcel in) {
        this.text = in.readString();
        this.value = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<Distance_> CREATOR = new Creator<Distance_>() {
        @Override
        public Distance_ createFromParcel(Parcel source) {
            return new Distance_(source);
        }

        @Override
        public Distance_[] newArray(int size) {
            return new Distance_[size];
        }
    };
}
