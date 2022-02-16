package com.pclink.attendance.system.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class ReportGenerateModel {

    @SerializedName("AgencyID")
    @Expose
    public Integer agencyID;
    @SerializedName("CheckInID")
    @Expose
    public String checkInID;
    @SerializedName("Date")
    @Expose
    public String date;
    @SerializedName("FormType")
    @Expose
    public String formType;
    @SerializedName("version")
    @Expose
    public String version;


    /**
     * No args constructor for use in serialization
     *
     */
    public ReportGenerateModel() {
    }

    /**
     *
     * @param formType
     * @param date
     * @param checkInID
     * @param agencyID
     */
    public ReportGenerateModel(Integer agencyID, String checkInID, String date, String formType, String version)
    {
        this.agencyID = agencyID;
        this.checkInID = checkInID;
        this.date = date;
        this.formType = formType;
        this.version = version;
    }


}