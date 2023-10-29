package com.warungku.utils.api.response;

import com.warungku.models.product.ProductModel;

public class ResponseProduct {
    public int code;
    public String message;
    public ProductModel data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ProductModel getData() {
        return data;
    }

    public void setData(ProductModel data) {
        this.data = data;
    }
}
