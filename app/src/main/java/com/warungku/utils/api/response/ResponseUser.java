package com.warungku.utils.api.response;

import com.warungku.models.user.UserModel;

public class ResponseUser {
    private Integer code;
    private String message;
    private UserModel data;
    private Integer trx_today;
    private Integer trx_week;

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

    public Integer getTrxWeek() {
        return trx_week;
    }

    public void setTrxWeek(Integer trxWeek) {
        this.trx_week = trxWeek;
    }

    public Integer getTrxToday() {
        return trx_today;
    }

    public void setTrxToday(Integer trxMonth) {
        this.trx_today = trxMonth;
    }
}
