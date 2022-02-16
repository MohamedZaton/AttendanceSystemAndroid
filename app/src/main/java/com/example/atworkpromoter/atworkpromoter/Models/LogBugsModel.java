package com.pclink.attendance.system.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LogBugsModel {
    @SerializedName("title")
    @Expose
    private String title ;
    @SerializedName("msg")
    @Expose
    private String msg ;
    @SerializedName("date")
    @Expose
    private String date ;
    @SerializedName("url")
    @Expose
    private String url ;
    @SerializedName("checkType")
    @Expose
    private String checkType ;
    @SerializedName("checkInID")
    @Expose
    private String checkInID ;
    @SerializedName("image")
    @Expose
    private String image ;
    @SerializedName("Lat")
    @Expose
    private String lat ;

    public LogBugsModel(String title, String msg, String url, String checkType, String checkInID, String image , String lat , String lng) {
        this.title = title;
        this.msg = msg;
        this.date = getDate();
        this.url = url;
        this.checkType = checkType;
        this.checkInID = checkInID;
        this.image = image;
        this.lat =lat;
        this.lng = lng ;
    }

    public LogBugsModel() {
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getCheckInID() {
        return checkInID;
    }

    public void setCheckInID(String checkInID) {
        this.checkInID = checkInID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    @SerializedName("Lng")
    @Expose
    private String lng ;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDate() {
        String pattern = "yyyy-MM-dd'T'HH:mm:ss a";
        DateFormat dateFormat = new SimpleDateFormat(pattern, new Locale("en"));
        Calendar calToday = Calendar.getInstance();
         date =dateFormat.format(calToday.getTime());
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
