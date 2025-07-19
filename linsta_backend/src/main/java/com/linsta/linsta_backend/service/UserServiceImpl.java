package com.linsta.linsta_backend.service;

import com.linsta.linsta_backend.request.ResetPasswordRequest;
import com.linsta.linsta_backend.request.UserLoginRequest;
import com.linsta.linsta_backend.request.UserRegisterRequest;
import com.linsta.linsta_backend.model.*;
import com.linsta.linsta_backend.repository.*;
import com.linsta.linsta_backend.response.UserLoginResponse;
import com.linsta.linsta_backend.response.UserRegisterResponse;
import com.linsta.linsta_backend.security.JwtUtil;
import com.linsta.linsta_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAddressRepository addressRepository;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserRegisterResponse register(UserRegisterRequest request) {
        System.out.println("EMAIL: " + request.getEmail());
        System.out.println("PHONE: " + request.getPhone());

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new UserRegisterResponse("Email đã tồn tại", "");
        }
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            return new UserRegisterResponse("Số điện thoại đã tồn tại", "");
        }

        UserAddress address = addressRepository
                .findByAddress(request.getAddress())
                .orElseGet(() -> addressRepository.save(new UserAddress(null, request.getAddress())));

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setAddress(address);
        user.setCreatedAt(LocalDateTime.now());

        user.setRole(new Role(1L, null));

        User savedUser = userRepository.save(user);

        String token = jwtTokenUtil.generateToken(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole().getName()
        );

        return new UserRegisterResponse("Đăng ký thành công", token);
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        System.out.println("EMAIL: " + request.getEmail());
        System.out.println("PASS " + request.getPassword());
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return new UserLoginResponse("Email chưa đăng kí", "");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new UserLoginResponse("Sai mật khẩu", "");
        }

        String token = jwtTokenUtil.generateToken(user.getId(), user.getRole().getName(), user.getEmail(), user.getName());

        return new UserLoginResponse("Đăng nhập thành công", token);
    }

    @Override
    public UserLoginResponse resetPassword(ResetPasswordRequest request){
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

    }

}

