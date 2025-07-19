package com.linsta.linsta_backend.response;

public class UserLoginResponse {
    private String message;
    private String token;

    public UserLoginResponse(String message, String token) {
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
