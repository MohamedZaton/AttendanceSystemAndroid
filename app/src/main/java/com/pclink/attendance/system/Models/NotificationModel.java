package com.pclink.attendance.system.Models;


import com.google.gson.annotations.SerializedName;

public class NotificationModel {
    @SerializedName("FromAgencyID")
    public Integer fromAgencyID;
    @SerializedName("ToAgencyID")
    public Integer toAgencyID;
    @SerializedName("Message")
    public String message;



    public NotificationModel(Integer fromAgencyID, Integer toAgencyID, String message) {
        super();
        this.fromAgencyID = fromAgencyID;
        this.toAgencyID = toAgencyID;
        this.message = message;
    }

}