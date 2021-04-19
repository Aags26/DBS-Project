package com.bphc.dbs_project.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.bphc.dbs_project.R;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Doctor implements Parcelable {

    @SerializedName("doc_id")
    public int docId;

    @SerializedName("name")
    public String name;

    @SerializedName("email")
    public String email;

    @SerializedName("phone")
    public String phone;

    @SerializedName("slots")
    public int[] slots;

    @SerializedName("hospital")
    public Hospital hospital;

    @SerializedName("department")
    public Department department;

    @SerializedName("image")
    public String image;

    public Doctor(String name, String email, String phone, Hospital hospital, Department department) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.hospital = hospital;
        this.department = department;
    }

    public Doctor(int docId, String name, String email, String phone, int[] slots, Hospital hospital, Department department, String image) {
        this.docId = docId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.slots = slots;
        this.hospital = hospital;
        this.department = department;
        this.image = image;
    }

    protected Doctor(Parcel in) {
        docId = in.readInt();
        name = in.readString();
        email = in.readString();
        phone = in.readString();
        slots = in.createIntArray();
        hospital = in.readParcelable(Hospital.class.getClassLoader());
        department = in.readParcelable(Department.class.getClassLoader());
        image = in.readString();
    }

    public static final Creator<Doctor> CREATOR = new Creator<Doctor>() {
        @Override
        public Doctor createFromParcel(Parcel in) {
            return new Doctor(in);
        }

        @Override
        public Doctor[] newArray(int size) {
            return new Doctor[size];
        }
    };

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public ArrayList<String> getSlots(Context context) {
        String[] s = context.getResources().getStringArray(R.array.slots);
        ArrayList<String> slotList = new ArrayList<>();
        for (int slot : slots) {
            slotList.add(s[slot]);
        }
        return slotList;
    }



    public void setSlots(ArrayList<Integer> slotList) {
        for (int i = 0; i < slotList.size(); i++) {
            slots[i] = slotList.get(0);
        }
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(docId);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeIntArray(slots);
        dest.writeParcelable(hospital, flags);
        dest.writeParcelable(department, flags);
        dest.writeString(image);
    }
}
