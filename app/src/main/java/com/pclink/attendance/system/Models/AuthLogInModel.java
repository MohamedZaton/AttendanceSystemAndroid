package com.pclink.attendance.system.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthLogInModel {

    @SerializedName("AgencyID")
    @Expose
    public String agencyID;
    @SerializedName("password")
    @Expose
    public String password;
    @SerializedName("version")
    @Expose
    public String version;
    @SerializedName("OS")
    @Expose
    public String oS;
    @SerializedName("OSVersion")
    @Expose
    public String oSVersion;
    @SerializedName("Device")
    @Expose
    public String device;
    @SerializedName("Network")
    @Expose
    public String network;

    /**
     * No args constructor for use in serialization
     *
     */
    public AuthLogInModel() {
    }

    /**
     *
     * @param password
     * @param oS
     * @param agencyID
     * @param version
     * @param device
     * @param oSVersion
     * @param network
     */
    public AuthLogInModel(String agencyID, String password, String version, String oS, String oSVersion, String device, String network) {
        super();
        this.agencyID = agencyID;
        this.password = password;
        this.version = version;
        this.oS = oS;
        this.oSVersion = oSVersion;
        this.device = device;
        this.network = network;

    }

}