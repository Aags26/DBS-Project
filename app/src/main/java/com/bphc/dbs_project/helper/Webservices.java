package com.bphc.dbs_project.helper;

import com.bphc.dbs_project.models.ServerResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static com.bphc.dbs_project.app.Constants.DOCTOR_REGISTER_URL;
import static com.bphc.dbs_project.app.Constants.HOSPITALS;
import static com.bphc.dbs_project.app.Constants.PATIENT_LOGIN_URL;
import static com.bphc.dbs_project.app.Constants.PATIENT_REGISTER_URL;

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
            @Field("age") int age,
            @Field("email") String email,
            @Field("password") String password,
            @Field("auth") int auth,
            @Field("hospital") String hospital,
            @Field("department") String department
    );

    @FormUrlEncoded
    @POST(PATIENT_LOGIN_URL)
    Call<ServerResponse> loginPatient(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST(HOSPITALS)
    Call<ServerResponse> getHospitals();


}
