package com.mobiquel.shank.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static Preferences instance;
    private String preferenceName = "com.mobiquel.urbanclap";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String KEY_EMAIL = "EMAIL";
    private String KEY_PHONE_NUMBER = "PHONE_NUMBER";
    private String KEY_PROFILE_PICTURE = "PROFILE_PICTURE";
    private String KEY_COMPANY_PICTURE = "COMPANY_PICTURE";

    private String KEY_USER_NAME = "USER_NAME";
    private String KEY_REFERRAL_CODE = "REFERAL_CODE";
    private String KEY_USER_ID = "USER_ID";
    private String KEY_USER_TYPE = "USER_TYPE";
    private String KEY_COMP_ID = "COMP_ID";

    private String KEY_COMPANY_CITY = "COMPANY_CITY";
    private String KEY_COMPANY_CODE = "COMPANY_CODE";
    private String KEY_COMPANY_NAME = "COMPANY_NAME";

    private String KEY_GCM_REGISTRATION = "GCM_REGISTRATION";
    private String KEY_COMPANY_TYPE = "COMPANY_TYP";
    private String KEY_COMPANY_TYPE_VALUE = "COMPANY_TYPE_VALUE";

    private String KEY_TERMS_OF_USE = "TERMS_OF_USE";
    private String KEY_CONTACT_US = "CONTACT_US";
    private String KEY_HOW_IT_WORKS = "HOW_IT_WORKS";
    private String KEY_HR_REFERRAL = "HR_REFERRAL";

    private String IS_LOGGED_IN = "IS_LOGGED_IN";
    private String IS_APPROVED_CLICKED = "IS_APPROVED_CLICKED";
    private String IS_TECHNICIAN_VERIFIED = "IS_TECHNICIAN_VERIFIED";
    private String IS_PROFILE_STATUS_APPROVED = "PROFILE_STATUS_APPROVED";
    private String IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH";
    private String IS_REGISTERED = "IS_REGISTERED";

    public String userId,hrReferralCode,contactUs,termsOfUse,howItWorks,companyType,companyTypeValue, phoneNumber,companyId, userName, gcmRegistrationId,name, email,referralCode,userProfilePicture,companyProfilePicture,userType,companyName,companyCity,companyCode;
    public boolean isLoggedIn;
    public boolean isFirstLaunch;
    public boolean isRegistered;
    public boolean isTechnicianVerified;
    public boolean isProfileStatusApproved;
    public boolean isApprovedClicked;

    private Preferences() {

    }

    public synchronized static Preferences getInstance() {
        if (instance == null)
            instance = new Preferences();
        return instance;
    }

    public void loadPreferences(Context c) {
        preferences = c.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE);
        phoneNumber = preferences.getString(KEY_PHONE_NUMBER, "");
        userName = preferences.getString(KEY_USER_NAME, "");
        referralCode = preferences.getString(KEY_REFERRAL_CODE, "");

        email = preferences.getString(KEY_EMAIL, "");

        companyName = preferences.getString(KEY_COMPANY_NAME, "");
        companyCode = preferences.getString(KEY_COMPANY_CODE, "");
        companyCity = preferences.getString(KEY_COMPANY_CITY, "");
        companyId = preferences.getString(KEY_COMP_ID, "");

        gcmRegistrationId = preferences.getString(KEY_GCM_REGISTRATION, "");
        userId = preferences.getString(KEY_USER_ID, "");
        userProfilePicture = preferences.getString(KEY_PROFILE_PICTURE, "");
        companyProfilePicture = preferences.getString(KEY_COMPANY_PICTURE, "");
        companyType = preferences.getString(KEY_COMPANY_TYPE, "");

        userType = preferences.getString(KEY_USER_TYPE, "");
        companyTypeValue = preferences.getString(KEY_COMPANY_TYPE_VALUE, "");
        hrReferralCode = preferences.getString(KEY_HR_REFERRAL, "");

        howItWorks = preferences.getString(KEY_HOW_IT_WORKS, "");
        termsOfUse = preferences.getString(KEY_TERMS_OF_USE, "");
        contactUs = preferences.getString(KEY_CONTACT_US, "");

        isRegistered = preferences.getBoolean(IS_REGISTERED, false);
        isApprovedClicked = preferences.getBoolean(IS_APPROVED_CLICKED, false);
        isTechnicianVerified = preferences.getBoolean(IS_TECHNICIAN_VERIFIED, false);

        isLoggedIn = preferences.getBoolean(IS_LOGGED_IN, false);
        isFirstLaunch = preferences.getBoolean(IS_FIRST_LAUNCH, true);
        isProfileStatusApproved = preferences.getBoolean(IS_PROFILE_STATUS_APPROVED, false);

    }

    public void savePreferences(Context c) {
        preferences = c.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE_NUMBER, phoneNumber);
        editor.putString(KEY_USER_NAME, userName);

        editor.putString(KEY_COMPANY_NAME, companyName);
        editor.putString(KEY_COMPANY_CITY, companyCity);
        editor.putString(KEY_COMPANY_CODE, companyCode);
        editor.putString(KEY_COMP_ID, companyId);

        editor.putString(KEY_REFERRAL_CODE, referralCode);
        editor.putString(KEY_PROFILE_PICTURE, userProfilePicture);
        editor.putString(KEY_COMPANY_PICTURE, companyProfilePicture);
        editor.putString(KEY_COMPANY_TYPE, companyType);
        editor.putString(KEY_COMPANY_TYPE_VALUE, companyTypeValue);

        editor.putString(KEY_CONTACT_US, contactUs);
        editor.putString(KEY_TERMS_OF_USE, termsOfUse);
        editor.putString(KEY_HOW_IT_WORKS, howItWorks);
        editor.putString(KEY_HR_REFERRAL, hrReferralCode);

        editor.putString(KEY_USER_TYPE, userType);

        editor.putString(KEY_GCM_REGISTRATION, gcmRegistrationId);
        editor.putString(KEY_USER_ID, userId);
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn);
        editor.putBoolean(IS_FIRST_LAUNCH, isFirstLaunch);
        editor.putBoolean(IS_REGISTERED, isRegistered);
        editor.putBoolean(IS_APPROVED_CLICKED, isApprovedClicked);
        editor.putBoolean(IS_TECHNICIAN_VERIFIED, isTechnicianVerified);
        editor.putBoolean(IS_PROFILE_STATUS_APPROVED, isProfileStatusApproved);

        editor.commit();
    }
}