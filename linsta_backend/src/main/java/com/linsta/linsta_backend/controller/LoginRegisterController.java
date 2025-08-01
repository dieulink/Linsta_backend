package com.linsta.linsta_backend.controller;

import com.linsta.linsta_backend.model.User;
import com.linsta.linsta_backend.repository.UserRepository;
import com.linsta.linsta_backend.request.OtpRequest;
import com.linsta.linsta_backend.request.ResetPasswordRequest;
import com.linsta.linsta_backend.request.UserLoginRequest;
import com.linsta.linsta_backend.request.UserRegisterRequest;
import com.linsta.linsta_backend.response.UserRegisterResponse;
import com.linsta.linsta_backend.service.EmailService;
import com.linsta.linsta_backend.service.RedisService;
import com.linsta.linsta_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;


@RequestMapping("/api/login_register")
@RestController
public class LoginRegisterController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequest request) {
            return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisService redisService;

    @PostMapping("/otp")
    public ResponseEntity<?> sendOtp(@RequestBody OtpRequest request) {

        if (!userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.ok(null);
        }

        String otp = generateOTP();
        emailService.sendOtpEmail(request.getEmail(), otp);
        redisService.saveOTP(request.getEmail(), otp);
        System.out.println("OTP " + redisService.getOTP(request.getEmail()));

        return ResponseEntity.ok("OTP đã được gửi tới email");
    }

    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    @PostMapping("/reset_password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(userService.resetPassword(request));
    }
}
