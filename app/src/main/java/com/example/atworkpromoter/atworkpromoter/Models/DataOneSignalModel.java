package com.pclink.attendance.system.Models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public  class DataOneSignalModel {

    @SerializedName("foo")
    @Expose
    public String foo;

    /**
     * No args constructor for use in serialization
     *
     */
    public DataOneSignalModel() {
    }

    /**
     *
     * @param foo
     */
    public DataOneSignalModel(String foo) {
        super();
        this.foo = foo;
    }

}
