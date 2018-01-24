package com.mobiquel.shank.model;

public class LeadsModel {

    private String title,date;

    public LeadsModel() {
    }

    public LeadsModel(String title, String date) {
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
