package com.linsta.linsta_backend.service;

import com.linsta.linsta_backend.model.User;
import com.linsta.linsta_backend.repository.UserRepository;
import com.linsta.linsta_backend.response.UserLoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    @Autowired
    private UserRepository userRepository;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("LINSTA _ MÃ XÁC NHẬN OTP");
        message.setText("Mã OTP của bạn là : " + otp + "\nMã OTP có hiệu lực trong thời gian 5 phút !");
        mailSender.send(message);
    }
}
