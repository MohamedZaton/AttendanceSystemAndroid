package com.pclink.attendance.system.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductModel {
  @SerializedName("id")
  @Expose
  private Integer id;
  @SerializedName("productName")
  @Expose
  private String productName;
  @SerializedName("sku")
  @Expose
  private String sku;
  @SerializedName("agencyID")
  @Expose
  private Integer agencyID;
  @SerializedName("status")
  @Expose
  private Object status;
  @SerializedName("categoryID")
  @Expose
  private Integer categoryID;
  @SerializedName("familyProductID")
  @Expose
  private Integer familyProductID;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public Integer getAgencyID() {
    return agencyID;
  }

  public void setAgencyID(Integer agencyID) {
    this.agencyID = agencyID;
  }

  public Object getStatus() {
    return status;
  }

  public void setStatus(Object status) {
    this.status = status;
  }

  public Integer getCategoryID() {
    return categoryID;
  }

  public void setCategoryID(Integer categoryID) {
    this.categoryID = categoryID;
  }

  public Integer getFamilyProductID() {
    return familyProductID;
  }

  public void setFamilyProductID(Integer familyProductID) {
    this.familyProductID = familyProductID;
  }

}