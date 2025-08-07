package com.linsta.linsta_backend.service;

import com.linsta.linsta_backend.request.*;
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

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserRegisterResponse register(UserRegisterRequest request) {
//        System.out.println("EMAIL: " + request.getEmail());
//        System.out.println("PHONE: " + request.getPhone());

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new UserRegisterResponse("Email đã tồn tại", null);
        }
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            return new UserRegisterResponse("Số điện thoại đã tồn tại", null);
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


        Role role = roleRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role"));
        user.setRole(role);

        System.out.println("roleeeeeeeeeeeeeeeeeee " + role.getName());
        userRepository.save(user);

        Optional<User> userOptional = userRepository.findByEmail(user.getEmail());

        User savedUser = userOptional.get();

        String token = jwtTokenUtil.generateToken(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                role.getName(),
                savedUser.getAddress().getAddress(),
                savedUser.getPhone()

        );

        return new UserRegisterResponse("Đăng ký thành công", token);
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        System.out.println("EMAIL: " + request.getEmail());
        System.out.println("PASS " + request.getPassword());
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return new UserLoginResponse("Email chưa đăng kí", null);
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new UserLoginResponse("Sai mật khẩu", null);
        }

        String token = jwtTokenUtil.generateToken(user.getId(), user.getName(), user.getEmail(), user.getRole().getName(), user.getAddress().getAddress(),                user.getPhone());

        return new UserLoginResponse("Đăng nhập thành công", token);
    }

    @Autowired
    private RedisService redisService;

    @Override
    public UserLoginResponse resetPassword(ResetPasswordRequest request){
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        // nếu đúng là otp của email đó thì cho đổi pass, sau đó trả về response với token bằng null

        User user = userOptional.get();
        if (request.getOtp().equals(redisService.getOTP(request.getEmail()))) {
            user.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));
            User savedUser = userRepository.save(user);
            redisService.deleteOTP(request.getEmail());
            Role role = roleRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy role"));
            String token = jwtTokenUtil.generateToken(
                    savedUser.getId(),
                    savedUser.getName(),
                    savedUser.getEmail(),
                    role.getName(),
                    savedUser.getAddress().getAddress(),
                    savedUser.getPhone()
            );


            return new UserLoginResponse("Đổi mật khẩu thành công", token);
        }
        return new UserLoginResponse("Đổi mật khẩu thất bại", null);
    }

    public UserLoginResponse editName(EditNameRequest request) {
        Optional<User> userOptional = userRepository.findById(request.getId());
        User user = userOptional.get();
        if (user == null) {
            return new UserLoginResponse("Người dùng không tồn tại", null);
        }

        user.setName(request.getNewName());
        userRepository.save(user);

        Role role = roleRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role"));
        String token = jwtTokenUtil.generateToken(
                user.getId(),
                user.getName(),
                user.getEmail(),
                role.getName(),
                user.getAddress().getAddress(),
                user.getPhone()
        );

        return new UserLoginResponse("Đổi tên thành công", token);
    }
    public UserLoginResponse editAddress(EditAddressRequest request) {
        Optional<User> userOptional = userRepository.findById(request.getId());
        if (userOptional.isEmpty()) {
            return new UserLoginResponse("Người dùng không tồn tại", null);
        }

        User user = userOptional.get();

        UserAddress address = addressRepository
                .findByAddress(request.getNewAddress())
                .orElseGet(() -> addressRepository.save(new UserAddress(null, request.getNewAddress())));

        user.setAddress(address);
        userRepository.save(user);

        Role role = roleRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role"));

        String token = jwtTokenUtil.generateToken(
                user.getId(),
                user.getName(),
                user.getEmail(),
                role.getName(),
                user.getAddress().getAddress(),
                user.getPhone()
        );

        return new UserLoginResponse("Cập nhật địa chỉ thành công", token);
    }

}

