package com.linsta.linsta_backend.request;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String email;
    private String password;
}
