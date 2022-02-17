package com.pclink.attendance.system.Wrapper;

import com.pclink.attendance.system.Models.ProductFamilyModel;

import java.util.List;

public class FamilyWrapper {
    private List<ProductFamilyModel>productFamilies;

    public List<ProductFamilyModel> getProductFamilies() {
        return productFamilies;
    }

    public void setProductFamilies(List<ProductFamilyModel> productFamilies) {
        this.productFamilies = productFamilies;

    }
}
