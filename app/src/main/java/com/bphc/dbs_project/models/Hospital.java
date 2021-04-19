package com.bphc.dbs_project.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Hospital implements Parcelable {

    @SerializedName("hid")
    public int hospitalId;

    @SerializedName("hname")
    public String hospitalName;

    @SerializedName("street")
    public String hospitalStreet;

    @SerializedName("area")
    public String hospitalArea;

    @SerializedName("count")
    public int count;

    public Hospital(String hospitalName, String hospitalStreet, String hospitalArea, int count) {
        this.hospitalName = hospitalName;
        this.hospitalStreet = hospitalStreet;
        this.hospitalArea = hospitalArea;
        this.count = count;
    }

    public Hospital(int hospitalId, String hospitalName, String hospitalStreet, String hospitalArea, int count) {
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.hospitalStreet = hospitalStreet;
        this.hospitalArea = hospitalArea;
        this.count = count;
    }


    protected Hospital(Parcel in) {
        hospitalId = in.readInt();
        hospitalName = in.readString();
        hospitalStreet = in.readString();
        hospitalArea = in.readString();
        count = in.readInt();
    }

    public static final Creator<Hospital> CREATOR = new Creator<Hospital>() {
        @Override
        public Hospital createFromParcel(Parcel in) {
            return new Hospital(in);
        }

        @Override
        public Hospital[] newArray(int size) {
            return new Hospital[size];
        }
    };

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getHospitalStreet() {
        return hospitalStreet;
    }

    public void setHospitalStreet(String hospitalStreet) {
        this.hospitalStreet = hospitalStreet;
    }

    public String getHospitalArea() {
        return hospitalArea;
    }

    public void setHospitalArea(String hospitalArea) {
        this.hospitalArea = hospitalArea;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(hospitalId);
        dest.writeString(hospitalName);
        dest.writeString(hospitalStreet);
        dest.writeString(hospitalArea);
        dest.writeInt(count);
    }
}
