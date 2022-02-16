package com.pclink.attendance.system.Models;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

public class LogCheckdataModel {
    @SerializedName("id")
    private  String id;
    @SerializedName("lat")
    private  String lat;

@SerializedName("lng")
    private  String lng;

@SerializedName("checkType")
    private  String checkType;

@SerializedName("imagePath")
    private  String imagePath;

@SerializedName("checkTime")
    private  String checkTime;

@SerializedName("checkInID")
    private  String checkInID;

@SerializedName("checkDate")
    private  String checkDate;

    @SerializedName("salesFormID")
    private  String salesFormID;


    @SerializedName("stillThereStatus")
    private  String stillThereStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }

    public String getCheckInID() {
        return checkInID;
    }

    public void setCheckInID(String checkInID) {
        this.checkInID = checkInID;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    public String getSalesFormID() {
        return salesFormID;
    }

    public void setSalesFormID(String salesFormID) {
        this.salesFormID = salesFormID;
    }

    public String getStillThereStatus() {
        if(stillThereStatus==null)
        {
            stillThereStatus="";

        }
        return stillThereStatus;
    }

    public void setStillThereStatus(String stillThereStatus) {
        this.stillThereStatus = stillThereStatus;
    }

}
