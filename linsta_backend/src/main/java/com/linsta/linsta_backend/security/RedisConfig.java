//package com.linsta.linsta_backend.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import java.time.Duration;
//
//@Configuration
//public class RedisConfig {
//
//    @Bean
//    public LettuceConnectionFactory redisConnectionFactory() {
//        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
//        redisConfig.setHostName("redis-12833.c1.ap-southeast-1-1.ec2.redns.redis-cloud.com");
//        redisConfig.setPort(12833);
//        redisConfig.setPassword("sTzqbLKHngjNtQRUcZtMMqYUoMGSDJmh");
//
//        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
//                .useSsl() // bắt buộc nếu Redis yêu cầu SSL
//                .commandTimeout(Duration.ofSeconds(5))
//                .build();
//
//        return new LettuceConnectionFactory(redisConfig, clientConfig);
//    }
//
//    @Bean
//    public RedisTemplate<?, ?> redisTemplate() {
//        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
//        template.setConnectionFactory(redisConnectionFactory());
//        return template;
//    }
//}
