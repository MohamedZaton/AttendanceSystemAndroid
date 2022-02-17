package com.pclink.attendance.system.Models;

import com.google.gson.annotations.SerializedName;

public class LogExcusesModel {
    @SerializedName("id")
    private  String id;
    @SerializedName("exMessage")
    private  String exMessage ;
    @SerializedName("status")
    private  String status ;
     @SerializedName("exid")
    private  String exid ;
  @SerializedName("exName")
    private  String exName;
  @SerializedName("userName")
    private  String userName ;
    @SerializedName("date")
    private  String date ;
  @SerializedName("agencyID")
    private  String agencyID ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExMessage() {
        return exMessage;
    }

    public void setExMessage(String exMessage) {
        this.exMessage = exMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExid() {
        return exid;
    }

    public void setExid(String exid) {
        this.exid = exid;
    }

    public String getExName() {
        return exName;
    }

    public void setExName(String exName) {
        this.exName = exName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAgencyID() {
        return agencyID;
    }

    public void setAgencyID(String agencyID) {
        this.agencyID = agencyID;
    }
}
