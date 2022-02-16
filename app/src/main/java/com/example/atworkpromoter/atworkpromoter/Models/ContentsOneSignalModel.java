package com.pclink.attendance.system.Models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public  class ContentsOneSignalModel {

    @SerializedName("en")
    @Expose
    public String en;



    public ContentsOneSignalModel(String EngMsg) {
        super();
        this.en = EngMsg;
    }

}
