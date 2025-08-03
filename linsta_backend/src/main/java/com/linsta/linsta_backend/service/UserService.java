package com.linsta.linsta_backend.service;

import com.linsta.linsta_backend.request.EditNameRequest;
import com.linsta.linsta_backend.request.ResetPasswordRequest;
import com.linsta.linsta_backend.request.UserLoginRequest;
import com.linsta.linsta_backend.request.UserRegisterRequest;
import com.linsta.linsta_backend.model.User;
import com.linsta.linsta_backend.response.UserLoginResponse;
import com.linsta.linsta_backend.response.UserRegisterResponse;

public interface UserService {
    UserRegisterResponse register(UserRegisterRequest request);
    UserLoginResponse login(UserLoginRequest request);
    UserLoginResponse resetPassword(ResetPasswordRequest request);
    UserLoginResponse editName(EditNameRequest request);
}
