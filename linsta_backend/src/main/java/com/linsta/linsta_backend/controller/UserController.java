package com.linsta.linsta_backend.controller;

import com.linsta.linsta_backend.request.EditAddressRequest;
import com.linsta.linsta_backend.request.EditNameRequest;
import com.linsta.linsta_backend.response.UserLoginResponse;
import com.linsta.linsta_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/edit_name")
    public ResponseEntity<?> editName(@RequestBody EditNameRequest request) {
        return ResponseEntity.ok(userService.editName(request));
    }
    @PostMapping("/edit_address")
    public ResponseEntity<UserLoginResponse> editAddress(@RequestBody EditAddressRequest request) {
        return ResponseEntity.ok(userService.editAddress(request));
    }

}
