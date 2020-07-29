package com.doan.timnhatro.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Comments implements Parcelable {
    public String comment, date, time, username, avatar;

    public Comments(String comment, String date, String time, String username, String avatar) {
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.username = username;
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvartar(String avatar) {
        this.avatar = avatar;
    }

    public Comments(){

    }
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.comment);
        dest.writeString(this.avatar);
        dest.writeString(this.date);
        dest.writeString(this.time);
        dest.writeString(this.username);
    }

    private Comments(Parcel in) {
        this.comment = in.readString();
        this.avatar = in.readString();
        this.date = in.readString();
        this.time = in.readString();
        this.username = in.readString();
    }

    public static final Creator<Comments> CREATOR = new Creator<Comments>() {
        @Override
        public Comments createFromParcel(Parcel source) {
            return new Comments(source);
        }

        @Override
        public Comments[] newArray(int size) {
            return new Comments[size];
        }
    };

}
