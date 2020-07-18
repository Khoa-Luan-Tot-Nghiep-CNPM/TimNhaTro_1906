package com.doan.timnhatro.model;

import android.os.Parcel;
import android.os.Parcelable;

public class LikePost implements Parcelable {

    private String id;
    private MotelRoom motelRoom;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MotelRoom getMotelRoom() {
        return motelRoom;
    }

    public void setMotelRoom(MotelRoom motelRoom) {
        this.motelRoom = motelRoom;
    }

    public LikePost(){
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeParcelable(this.motelRoom, i);
    }

    private LikePost(Parcel in){
        this.id = in.readString();
        this.motelRoom = in.readParcelable(MotelRoom.class.getClassLoader());
    }

    public static final Creator<LikePost> CREATOR = new Creator<LikePost>() {
        @Override
        public LikePost createFromParcel(Parcel parcel) {
            return new LikePost(parcel);
        }

        @Override
        public LikePost[] newArray(int i) {
            return new LikePost[i];
        }
    };
}
