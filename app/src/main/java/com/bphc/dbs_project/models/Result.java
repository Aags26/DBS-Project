package com.bphc.dbs_project.models;

import android.content.Context;

import com.bphc.dbs_project.R;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Result {

    @SerializedName("profession")
    public String profession;

    @SerializedName("patient")
    public Patient patient;

    @SerializedName("doctor")
    public Doctor doctor;

    @SerializedName("doctors")
    public ArrayList<Doctor> doctorsOfDepartment;

    @SerializedName("hospital")
    public Hospital hospital;

    @SerializedName("hospitals")
    public ArrayList<Hospital> hospitals;

    @SerializedName("departments")
    public ArrayList<Department> departments;

    @SerializedName("doctorDepartment")
    public ArrayList<Department> doctorDepartments;

    @SerializedName("doctorHospital")
    public ArrayList<Hospital> doctorHospitals;

    @SerializedName("today")
    public int[] todaySlots;

    @SerializedName("date1")
    public int[] date1Slots;

    @SerializedName("date2")
    public int[] date2Slots;

    @SerializedName("date3")
    public int[] date3Slots;

    @SerializedName("previous")
    public ArrayList<Appointment> previousAppointments;

    @SerializedName("todayAppointments")
    public ArrayList<Appointment> todayAppointments;

    @SerializedName("upcoming")
    public ArrayList<Appointment> upcomingAppointments;

    @SerializedName("medicalRecords")
    public ArrayList<MedicalRecord> medicalRecords;

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public ArrayList<Doctor> getDoctorsOfDepartment() {
        return doctorsOfDepartment;
    }

    public void setDoctorsOfDepartment(ArrayList<Doctor> doctorsOfDepartment) {
        this.doctorsOfDepartment = doctorsOfDepartment;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public ArrayList<Hospital> getHospitals() {
        return hospitals;
    }

    public void setHospitals(ArrayList<Hospital> hospitals) {
        this.hospitals = hospitals;
    }

    public ArrayList<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(ArrayList<Department> departments) {
        this.departments = departments;
    }

    public ArrayList<Department> getDoctorDepartments() {
        return doctorDepartments;
    }

    public void setDoctorDepartments(ArrayList<Department> doctorDepartments) {
        this.doctorDepartments = doctorDepartments;
    }

    public ArrayList<Hospital> getDoctorHospitals() {
        return doctorHospitals;
    }

    public void setDoctorHospitals(ArrayList<Hospital> doctorHospitals) {
        this.doctorHospitals = doctorHospitals;
    }

    public ArrayList<String> getTodaySlots(Context context) {
        String[] arr = context.getResources().getStringArray(R.array.slots);
        ArrayList<String> todaySlotsList = new ArrayList<>();
        for (int i: todaySlots)
            todaySlotsList.add(arr[i]);
        return todaySlotsList;
    }

    public ArrayList<String> getDate1Slots(Context context) {
        String[] arr = context.getResources().getStringArray(R.array.slots);
        ArrayList<String> date1SlotsList = new ArrayList<>();
        for (int i: date1Slots)
            date1SlotsList.add(arr[i]);
        return date1SlotsList;
    }

    public ArrayList<String> getDate2Slots(Context context) {
        String[] arr = context.getResources().getStringArray(R.array.slots);
        ArrayList<String> date2SlotsList = new ArrayList<>();
        for (int i: date2Slots)
            date2SlotsList.add(arr[i]);
        return date2SlotsList;
    }

    public ArrayList<String> getDate3Slots(Context context) {
        String[] arr = context.getResources().getStringArray(R.array.slots);
        ArrayList<String> date3SlotsList = new ArrayList<>();
        for (int i: date3Slots)
            date3SlotsList.add(arr[i]);
        return date3SlotsList;
    }

    public ArrayList<Appointment> getPreviousAppointments() {
        return previousAppointments;
    }

    public void setPreviousAppointments(ArrayList<Appointment> previousAppointments) {
        this.previousAppointments = previousAppointments;
    }

    public ArrayList<Appointment> getTodayAppointments() {
        return todayAppointments;
    }

    public void setTodayAppointments(ArrayList<Appointment> todayAppointments) {
        this.todayAppointments = todayAppointments;
    }

    public ArrayList<Appointment> getUpcomingAppointments() {
        return upcomingAppointments;
    }

    public void setUpcomingAppointments(ArrayList<Appointment> upcomingAppointments) {
        this.upcomingAppointments = upcomingAppointments;
    }

    public ArrayList<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    public void setMedicalRecords(ArrayList<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }
}