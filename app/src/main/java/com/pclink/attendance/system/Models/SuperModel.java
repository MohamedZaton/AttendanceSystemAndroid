package com.pclink.attendance.system.Models;

import com.google.gson.annotations.SerializedName;

public class SuperModel {
    @SerializedName("name")
    private  String sName;
    @SerializedName("agencyID")
    private  int sAgencyID;
    @SerializedName("email")
    private String sEmail;
    @SerializedName("phone")
    private  String sPhone;

    public String getsName() {

        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsEmail() {
        return sEmail;
    }

    public void setsEmail(String sEmail) {
        this.sEmail = sEmail;
    }

    public String getsPhone() {
        return sPhone;
    }

    public void setsPhone(String sPhone) {
        this.sPhone = sPhone;
    }

    public int getsAgencyID() {
        return sAgencyID;
    }

    public void setsAgencyID(int sAgencyID) {
        this.sAgencyID = sAgencyID;
    }
}
