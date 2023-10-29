package com.warungku.utils.api.response;

import com.warungku.models.user.UserModel;

public class ResponseRegister {
    private Integer code;
    private String message;
    private UserModel data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserModel getData() {
        return data;
    }

    public void setData(UserModel data) {
        this.data = data;
    }
}
