package com.bphc.dbs_project.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Appointment implements Parcelable {

    @SerializedName("time")
    public String time;

    @SerializedName("doctor")
    public Doctor doctor;

    @SerializedName("patient")
    public Patient patient;

    public Appointment(String time, Doctor doctor, Patient patient) {
        this.time = time;
        this.doctor = doctor;
        this.patient = patient;
    }

    protected Appointment(Parcel in) {
        time = in.readString();
        doctor = in.readParcelable(Doctor.class.getClassLoader());
        patient = in.readParcelable(Patient.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
        dest.writeParcelable(doctor, flags);
        dest.writeParcelable(patient, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Appointment> CREATOR = new Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel in) {
            return new Appointment(in);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
