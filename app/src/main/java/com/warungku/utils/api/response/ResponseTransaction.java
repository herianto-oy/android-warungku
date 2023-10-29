package com.warungku.utils.api.response;

import com.warungku.models.pos.TransactionModel;

public class ResponseTransaction {
    public int code;
    public String message;
    public TransactionModel data;

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

    public TransactionModel getData() {
        return data;
    }

    public void setData(TransactionModel data) {
        this.data = data;
    }
}
