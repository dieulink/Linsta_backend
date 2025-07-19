package com.linsta.linsta_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void saveOTP(String email, String otp) {
        redisTemplate.opsForValue().set(email, otp, Duration.ofMinutes(5)); // TTL 5 phút
    }

    public String getOTP(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    public void deleteOTP(String email) {
        redisTemplate.delete(email);
    }
}
