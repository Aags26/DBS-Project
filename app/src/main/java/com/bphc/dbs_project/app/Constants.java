package com.bphc.dbs_project.app;

public class Constants {

    public static final String IMGUR_CLIENT_ID = "f380020d3fe0efd";
    public static final String IMGUR_CLIENT_SECRET = "7f2edb4ebcbb5e4abdc8eeafafd54c70b0b66131";

    public static final String BASE_URL = "https://fold-preservation.000webhostapp.com/";

    public static final String LOGIN_URL = "login.php";

    public static final String PATIENT_REGISTER_URL = "registerPatient.php";
    public static final String DOCTOR_REGISTER_URL = "registerDoctor.php";

    public static final String CHECK_EMAIL = "checkEmail.php";
    public static final String GET_OTP = "sendOTP.php";
    public static final String VERIFY_OTP = "checkOTP.php";


    public static final String HOSPITALS = "Hospital.php";
    public static final String REGISTER_HOSPITAL = "registerHospital.php";

    public static final String CHECK_HOSP_DEPT = "checkHospDept.php";

    public static final String DEPARTMENTS = "Department.php";
    public static final String NUM_DOCTORS_DEPARTMENT = "numDoctorDepartment.php";
    public static final String NUM_DOCTORS_HOSPITAL = "numDoctorHospital.php";
    public static final String DOCTORS_OF_DEPARTMENT = "departmentwise.php";
    public static final String DOCTORS_OF_HOSPITAL = "hospitalwise.php";

    public static final String BOOK_APPOINTMENT = "bookAppointment.php";
    public static final String AVAILABLE_APPOINTMENTS = "doctorAppointment.php";
    public static final String CHECK_CLASH = "checkFree.php";
    public static final String PATIENT_APPOINTMENTS = "patientAppointment.php";
    public static final String CHANGE_APPOINTMENT = "deleteAppointment.php";

    public static final String CREATE_MEDICAL_RECORD = "createMedicalRecord.php";
    public static final String PATIENT_MEDICAL_RECORDS = "MedicalRecord.php";
    public static final String DELETE_MEDICAL_RECORD = "deleteMedicalRecord.php";

    public static final String PATIENT_PROFILE = "Patient.php";
    public static final String UPLOAD_IMAGE = "uploadImage.php";

    public static final String DOCTORS_APPOINTMENTS = "doctorsAppointments.php";
    public static final String DOCTOR_PROFILE = "Doctor.php";

    public static String getImgurClientId() {
        return "Client-ID " + IMGUR_CLIENT_ID;
    }

}
