package com.pclink.attendance.system.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductFamilyModel {@SerializedName("id")
@Expose
private Integer id;
    @SerializedName("familyName")
    @Expose
    private String familyName;
    @SerializedName("categoryID")
    @Expose
    private Integer categoryID;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public Integer getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Integer categoryID) {
        this.categoryID = categoryID;
    }

}