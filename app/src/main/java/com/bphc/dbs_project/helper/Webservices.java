package com.bphc.dbs_project.helper;

import com.bphc.dbs_project.models.ImageResponse;
import com.bphc.dbs_project.models.ServerResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

import static com.bphc.dbs_project.app.Constants.AVAILABLE_APPOINTMENTS;
import static com.bphc.dbs_project.app.Constants.BOOK_APPOINTMENT;
import static com.bphc.dbs_project.app.Constants.CHANGE_APPOINTMENT;
import static com.bphc.dbs_project.app.Constants.CHECK_CLASH;
import static com.bphc.dbs_project.app.Constants.CHECK_EMAIL;
import static com.bphc.dbs_project.app.Constants.CHECK_HOSP_DEPT;
import static com.bphc.dbs_project.app.Constants.CREATE_MEDICAL_RECORD;
import static com.bphc.dbs_project.app.Constants.DELETE_MEDICAL_RECORD;
import static com.bphc.dbs_project.app.Constants.DEPARTMENTS;
import static com.bphc.dbs_project.app.Constants.DOCTORS_APPOINTMENTS;
import static com.bphc.dbs_project.app.Constants.DOCTORS_OF_DEPARTMENT;
import static com.bphc.dbs_project.app.Constants.DOCTORS_OF_HOSPITAL;
import static com.bphc.dbs_project.app.Constants.DOCTOR_PROFILE;
import static com.bphc.dbs_project.app.Constants.DOCTOR_REGISTER_URL;
import static com.bphc.dbs_project.app.Constants.GET_OTP;
import static com.bphc.dbs_project.app.Constants.HOSPITALS;
import static com.bphc.dbs_project.app.Constants.LOGIN_URL;
import static com.bphc.dbs_project.app.Constants.NUM_DOCTORS_DEPARTMENT;
import static com.bphc.dbs_project.app.Constants.NUM_DOCTORS_HOSPITAL;
import static com.bphc.dbs_project.app.Constants.PATIENT_APPOINTMENTS;
import static com.bphc.dbs_project.app.Constants.PATIENT_MEDICAL_RECORDS;
import static com.bphc.dbs_project.app.Constants.PATIENT_PROFILE;
import static com.bphc.dbs_project.app.Constants.PATIENT_REGISTER_URL;
import static com.bphc.dbs_project.app.Constants.REGISTER_HOSPITAL;
import static com.bphc.dbs_project.app.Constants.UPLOAD_IMAGE;
import static com.bphc.dbs_project.app.Constants.VERIFY_OTP;

public interface Webservices {

    @FormUrlEncoded
    @POST(PATIENT_REGISTER_URL)
    Call<ServerResponse> registerPatient(
            @Field("name") String name,
            @Field("phone") String phone,
            @Field("age") int age,
            @Field("address") String address,
            @Field("email") String email,
            @Field("password") String password,
            @Field("auth") int auth
    );

    @FormUrlEncoded
    @POST(DOCTOR_REGISTER_URL)
    Call<ServerResponse> registerDoctor(
            @Field("name") String name,
            @Field("phone") String phone,
            @Field("email") String email,
            @Field("password") String password,
            @Field("auth") int auth,
            @Field("dep_name") String department,
            @Field("hname") String hospital,
            @Field("slots[]") ArrayList<Integer> slots
    );

    @FormUrlEncoded
    @POST(CHECK_EMAIL)
    Call<ServerResponse> checkCredentials(
            @Field("email") String email,
            @Field("password") String password,
            @Field("auth") int auth
    );

    @FormUrlEncoded
    @POST(GET_OTP)
    Call<ServerResponse> getOtp(
            @Field("phone") String phone
    );

    @FormUrlEncoded
    @POST(VERIFY_OTP)
    Call<ServerResponse> verifyOtp(
            @Field("phone") String phone,
            @Field("otp") String otp
    );

    @FormUrlEncoded
    @POST(LOGIN_URL)
    Call<ServerResponse> loginPatient(
            @Field("email") String email,
            @Field("password") String password
    );

    @POST(HOSPITALS)
    Call<ServerResponse> getHospitals();

    @POST(DEPARTMENTS)
    Call<ServerResponse> getDepartments();

    @FormUrlEncoded
    @POST(REGISTER_HOSPITAL)
    Call<ServerResponse> registerHospital(
            @Field("hospital") String hospital,
            @Field("street") String street,
            @Field("area") String area
    );

    @FormUrlEncoded
    @POST(CHECK_HOSP_DEPT)
    Call<ServerResponse> checkHospDept(
            @Field("hname") String hospital,
            @Field("dep_name") String department
    );

    @FormUrlEncoded
    @POST(DOCTORS_OF_DEPARTMENT)
    Call<ServerResponse> getDoctorsOfDepartment(
            @Field("dep_id") int dep_id
    );

    @FormUrlEncoded
    @POST(DOCTORS_OF_HOSPITAL)
    Call<ServerResponse> getDoctorsOfHospital(
            @Field("hid") int hid
    );

    @POST(NUM_DOCTORS_DEPARTMENT)
    Call<ServerResponse> getAllDepartments();

    @POST(NUM_DOCTORS_HOSPITAL)
    Call<ServerResponse> getAllHospitals();

    @FormUrlEncoded
    @POST(BOOK_APPOINTMENT)
    Call<ServerResponse> bookAppointment(
            @Field("pid") int pid,
            @Field("doc_id") int doc_id,
            @Field("slot") int slot,
            @Field("time") String time
    );

    @FormUrlEncoded
    @POST(AVAILABLE_APPOINTMENTS)
    Call<ServerResponse> getAvailableAppointments(
            @Field("doc_id") int doc_id
    );

    @FormUrlEncoded
    @POST(CHECK_CLASH)
    Call<ServerResponse> checkClash(
            @Field("pid") int pid,
            @Field("time") String time
    );

    @FormUrlEncoded
    @POST(PATIENT_APPOINTMENTS)
    Call<ServerResponse> getAllPatientAppointments(
            @Field("pid") int pid
    );

    @FormUrlEncoded
    @POST(CHANGE_APPOINTMENT)
    Call<ServerResponse> deleteAppointment(
            @Field("pid") int pid,
            @Field("doc_id") int docId,
            @Field("slot") int slot,
            @Field("time") String prevTime
    );

    @FormUrlEncoded
    @POST(PATIENT_MEDICAL_RECORDS)
    Call<ServerResponse> getPatientMedicalRecords(
            @Field("pid") int pid
    );

    @FormUrlEncoded
    @POST(CREATE_MEDICAL_RECORD)
    Call<ServerResponse> createMedicalRecord(
            @Field("pid") int pid,
            @Field("doe") String doe,
            @Field("attachment") String attachment,
            @Field("diagnosis") String diagnosis,
            @Field("description") String description
    );

    @FormUrlEncoded
    @POST(DELETE_MEDICAL_RECORD)
    Call<ServerResponse> deleteMedicalRecord(
            @Field("mid") int mid
    );

    @FormUrlEncoded
    @POST(PATIENT_PROFILE)
    Call<ServerResponse> getPatientInfo(
            @Field("pid") int pid
    );

    @FormUrlEncoded
    @POST(UPLOAD_IMAGE)
    Call<ServerResponse> updateProfilePic(
            @Field("image") String image,
            @Field("profession") String profession,
            @Field("id") int id
    );

    @FormUrlEncoded
    @POST(DOCTORS_APPOINTMENTS)
    Call<ServerResponse> getDoctorAppointments(
            @Field("doc_id") int docId
    );

    @FormUrlEncoded
    @POST(DOCTOR_PROFILE)
    Call<ServerResponse> getDoctorInfo(
            @Field("doc_id") int docId
    );

    @FormUrlEncoded
    @POST("https://api.imgur.com/3/image")
    Call<ImageResponse> postImage(@Header("Authorization") String auth,
                                  @Field("image") String encoded);

}
