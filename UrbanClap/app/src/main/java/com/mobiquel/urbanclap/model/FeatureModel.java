package com.mobiquel.urbanclap.model;

/**
 * Created by landshark on 19/9/17.
 */

public class FeatureModel {

    private String title,date;

    public FeatureModel() {
    }

    public FeatureModel(String title, String date) {
        this.title = title;

        this.date = date;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
