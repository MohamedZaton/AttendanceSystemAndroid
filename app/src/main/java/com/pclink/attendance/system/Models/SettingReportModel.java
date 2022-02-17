package com.pclink.attendance.system.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SettingReportModel  {
    @SerializedName("FieldID")
    @Expose
    private String fieldID;
    @SerializedName("FieldName")
    @Expose
    private String fieldName;
    @SerializedName("Status")
    @Expose
    private String status;

    public String getFieldID() {
        return fieldID;
    }

    public void setFieldID(String fieldID) {
        this.fieldID = fieldID;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
