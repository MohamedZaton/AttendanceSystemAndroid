package com.pclink.attendance.system.Models;

import com.google.gson.annotations.SerializedName;

public class LogVacationsModel {

    @SerializedName("id")
    private  String id;

    @SerializedName("dateFrom")
    private  String dateFrom ;

    @SerializedName("dateTo")
    private  String dateTo ;

    @SerializedName("status")
    private  String status ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
