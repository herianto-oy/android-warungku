package com.warungku.utils.api.response;

import com.warungku.models.product.ProductModel;

import java.util.List;

public class ResponseProductList {

    public int current_page;
    public int last_page;
    public List<ProductModel> data;

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public List<ProductModel> getData() {
        return data;
    }

    public void setData(List<ProductModel> data) {
        this.data = data;
    }
}
