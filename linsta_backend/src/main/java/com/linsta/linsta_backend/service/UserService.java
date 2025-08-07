package com.linsta.linsta_backend.service;

import com.linsta.linsta_backend.request.*;
import com.linsta.linsta_backend.model.User;
import com.linsta.linsta_backend.response.UserLoginResponse;
import com.linsta.linsta_backend.response.UserRegisterResponse;

public interface UserService {
    UserRegisterResponse register(UserRegisterRequest request);
    UserLoginResponse login(UserLoginRequest request);
    UserLoginResponse resetPassword(ResetPasswordRequest request);
    UserLoginResponse editName(EditNameRequest request);
    UserLoginResponse editAddress(EditAddressRequest request);

}
