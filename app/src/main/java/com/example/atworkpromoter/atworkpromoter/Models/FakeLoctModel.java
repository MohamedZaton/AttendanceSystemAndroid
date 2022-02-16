package com.pclink.attendance.system.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FakeLoctModel {
    @SerializedName("AgencyID")
    @Expose
    private Integer agencyID;
    @SerializedName("Action")
    @Expose
    private String action;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Date")
    @Expose
    private String date;
    public FakeLoctModel() {
    }

    /**
     *
     * @param date
     * @param action
     * @param agencyID
     * @param status
     */
    public FakeLoctModel(Integer agencyID, String action, String status, String date) {
        super();
        this.agencyID = agencyID;
        this.action = action;
        this.status = status;
        this.date = date;
    }

    public Integer getAgencyID() {
        return agencyID;
    }

    public void setAgencyID(Integer agencyID) {
        this.agencyID = agencyID;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
