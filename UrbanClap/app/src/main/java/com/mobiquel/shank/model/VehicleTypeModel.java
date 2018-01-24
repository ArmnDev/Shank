package com.mobiquel.shank.model;

public class VehicleTypeModel {

    String text;
    Integer imageId;
    public VehicleTypeModel(String text, Integer imageId){
        this.text=text;
        this.imageId=imageId;
    }

    public String getText(){
        return text;
    }

    public Integer getImageId(){
        return imageId;
    }
}
