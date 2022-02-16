package com.pclink.attendance.system.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public  class FilterOneSignalModel {

    @SerializedName("field")
    @Expose
    public String field;
    @SerializedName("key")
    @Expose
    public String key;
    @SerializedName("relation")
    @Expose
    public String relation;
    @SerializedName("value")
    @Expose
    public String value;


    public FilterOneSignalModel(String field, String key, String relation, String value) {
        super();
        this.field = field;
        this.key = key;
        this.relation = relation;
        this.value = value;
    }

}