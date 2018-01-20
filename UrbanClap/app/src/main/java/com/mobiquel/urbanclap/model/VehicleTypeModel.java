package com.mobiquel.urbanclap.model;

/**
 * Created by landshark on 19/9/17.
 */

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
