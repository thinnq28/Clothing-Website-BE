package com.datn.shop_app.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RedisTokenService {
    private final StringRedisTemplate redisTemplate;

    public RedisTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveToken(String token, String email) {
        redisTemplate.opsForHash().put(token, "email", email);
        redisTemplate.opsForHash().put(token, "status", "valid"); // Trạng thái ban đầu là "valid"
        redisTemplate.expire(token, Duration.ofHours(24)); // Hết hạn sau 24h
    }

    // Kiểm tra xem token có tồn tại và chưa sử dụng hay không
    public boolean isUsedToken(String token) {
        Object status = redisTemplate.opsForHash().get(token, "status");
        return status != null && status.equals("used");
    }

    // Kiểm tra token còn hạn không
    public boolean isTokenExpired(String token) {
        Long ttl = redisTemplate.getExpire(token, TimeUnit.SECONDS); // Lấy TTL còn lại
        return (ttl == null || ttl <= 0); // Nếu TTL <= 0, token đã hết hạn
    }

    // Lấy email từ token (chỉ khi token hợp lệ)
    public String getEmailFromToken(String token) {
        if (!isUsedToken(token)) return null;
        return (String) redisTemplate.opsForHash().get(token, "email");
    }

    // Đánh dấu token là "used" sau khi reset password
    public void markTokenAsUsed(String token) {
        redisTemplate.opsForHash().put(token, "status", "used"); // Đánh dấu đã sử dụng
        redisTemplate.expire(token, Duration.ofMinutes(5)); // Token tự động xóa sau 5 phút
    }

    public void deleteToken(String token) {
        redisTemplate.delete(token);
    }
}
