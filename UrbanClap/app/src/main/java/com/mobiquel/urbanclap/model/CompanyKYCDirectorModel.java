package com.mobiquel.urbanclap.model;

/**
 * Created by landshark on 19/9/17.
 */

public class CompanyKYCDirectorModel {

    private String name,mobile,adhar;

    public CompanyKYCDirectorModel() {
    }

    public CompanyKYCDirectorModel(String name, String mobile, String adhar) {
        this.name = name;

        this.mobile = mobile;

        this.adhar = adhar;


    }

    public String getAdhar() {
        return adhar;
    }

    public void setAdhar(String adhar) {
        this.adhar = adhar;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }



    public String getmobile() {
        return mobile;
    }

    public void setmobile(String mobile) {
        this.mobile = mobile;
    }
}
