package com.bphc.dbs_project.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Department implements Parcelable {

    @SerializedName("dep_id")
    public int id;

    @SerializedName("dep_name")
    public String name;

    @SerializedName("count")
    public int count;

    public Department(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public Department(int id, String name, int count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

    protected Department(Parcel in) {
        id = in.readInt();
        name = in.readString();
        count = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(count);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Department> CREATOR = new Creator<Department>() {
        @Override
        public Department createFromParcel(Parcel in) {
            return new Department(in);
        }

        @Override
        public Department[] newArray(int size) {
            return new Department[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
