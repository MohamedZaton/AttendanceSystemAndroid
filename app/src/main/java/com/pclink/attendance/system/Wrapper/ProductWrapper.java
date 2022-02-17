package com.pclink.attendance.system.Wrapper;

import com.pclink.attendance.system.Models.ProductModel;

import java.util.List;

public class ProductWrapper {
    private List<ProductModel> products;

    public List<ProductModel> getProducts() {
        return products;
    }

    public void setProducts(List<ProductModel> products) {
        this.products = products;
    }
}
