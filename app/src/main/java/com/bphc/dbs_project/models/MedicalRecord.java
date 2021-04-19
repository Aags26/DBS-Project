package com.bphc.dbs_project.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MedicalRecord implements Parcelable {

    @SerializedName("mid")
    public int mid;

    @SerializedName("diagnosis")
    public String diagnosis;

    @SerializedName("doe")
    public String doe;

    @SerializedName("description")
    public String description;

    @SerializedName("attachment")
    public String attachment;

    public MedicalRecord(int mid, String diagnosis, String doe, String description, String attachment) {
        this.mid = mid;
        this.diagnosis = diagnosis;
        this.doe = doe;
        this.description = description;
        this.attachment = attachment;
    }

    protected MedicalRecord(Parcel in) {
        mid = in.readInt();
        diagnosis = in.readString();
        doe = in.readString();
        description = in.readString();
        attachment = in.readString();
    }

    public static final Creator<MedicalRecord> CREATOR = new Creator<MedicalRecord>() {
        @Override
        public MedicalRecord createFromParcel(Parcel in) {
            return new MedicalRecord(in);
        }

        @Override
        public MedicalRecord[] newArray(int size) {
            return new MedicalRecord[size];
        }
    };

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getDoe() {
        return doe;
    }

    public void setDoe(String doe) {
        this.doe = doe;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mid);
        dest.writeString(diagnosis);
        dest.writeString(doe);
        dest.writeString(description);
        dest.writeString(attachment);
    }
}
