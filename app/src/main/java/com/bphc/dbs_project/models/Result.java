package com.bphc.dbs_project.models;

import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("name")
    public String name;

    @SerializedName("email")
    public String email;

    @SerializedName("phone")
    public String phone;

    @SerializedName("address")
    public String address;

    @SerializedName("age")
    public int age;


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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
