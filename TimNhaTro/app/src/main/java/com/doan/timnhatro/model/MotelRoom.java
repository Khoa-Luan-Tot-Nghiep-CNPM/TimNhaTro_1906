package com.doan.timnhatro.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MotelRoom implements Parcelable {
    private String  id;
    private String  nameMotelRoom;
    private String  describe;
    private long    price;
    private String  street;
    private String  district;
    private String  city;
    private Account account;
    private Position  position;
    private ArrayList<String> arrayPicture;

    public MotelRoom() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getNameMotelRoom() {
        return nameMotelRoom;
    }

    public void setNameMotelRoom(String nameMotelRoom) {
        this.nameMotelRoom = nameMotelRoom;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public ArrayList<String> getArrayPicture() {
        return arrayPicture;
    }

    public void setArrayPicture(ArrayList<String> arrayPicture) {
        this.arrayPicture = arrayPicture;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.nameMotelRoom);
        dest.writeString(this.describe);
        dest.writeLong(this.price);
        dest.writeString(this.street);
        dest.writeString(this.district);
        dest.writeString(this.city);
        dest.writeParcelable(this.account, flags);
        dest.writeParcelable(this.position, flags);
        dest.writeStringList(this.arrayPicture);
    }

    private MotelRoom(Parcel in) {
        this.id = in.readString();
        this.nameMotelRoom = in.readString();
        this.describe = in.readString();
        this.price = in.readLong();
        this.street = in.readString();
        this.district = in.readString();
        this.city = in.readString();
        this.account = in.readParcelable(Account.class.getClassLoader());
        this.position = in.readParcelable(Position.class.getClassLoader());
        this.arrayPicture = in.createStringArrayList();
    }

    public static final Creator<MotelRoom> CREATOR = new Creator<MotelRoom>() {
        @Override
        public MotelRoom createFromParcel(Parcel source) {
            return new MotelRoom(source);
        }

        @Override
        public MotelRoom[] newArray(int size) {
            return new MotelRoom[size];
        }
    };
}
