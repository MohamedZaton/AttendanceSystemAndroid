package com.pclink.attendance.system.Wrapper;

import com.pclink.attendance.system.Models.ProductCategoryModel;

import java.util.List;

public class CategoryWrapper {
    private List<ProductCategoryModel> productCategories;

    public List<ProductCategoryModel> getProductCategories() {
        return productCategories;
    }

    public void setProductCategories(List<ProductCategoryModel> productCategories) {
        this.productCategories = productCategories;
    }
}
