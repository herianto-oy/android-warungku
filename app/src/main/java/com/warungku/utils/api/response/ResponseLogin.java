package com.warungku.utils.api.response;

import com.warungku.models.user.UserModel;

public class ResponseLogin {
    private Integer code;
    private String message;
    private ResData data;

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

    public ResData getData() {
        return data;
    }

    public void setData(ResData data) {
        this.data = data;
    }

    public class ResData {
        private UserModel user;
        private String api_token;

        public UserModel getUser() {
            return user;
        }

        public void setUser(UserModel user) {
            this.user = user;
        }

        public String getApi_token() {
            return api_token;
        }

        public void setApi_token(String api_token) {
            this.api_token = api_token;
        }

        @Override
        public String toString() {
            return "ResData{" +
                    "user=" + user +
                    ", api_token='" + api_token + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ResponseLogin{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
