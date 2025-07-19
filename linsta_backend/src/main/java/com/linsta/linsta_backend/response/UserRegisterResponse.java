package com.linsta.linsta_backend.response;

import lombok.Data;


public class UserRegisterResponse {
    private String message;
    private String token;

    public UserRegisterResponse(String message, String token) {
        this.message = message;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}
