package com.example.e_commerce.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final StringRedisTemplate redisTemplate;

    private static final Duration REFRESH_TTL = Duration.ofDays(7);
    private static final String KEY_PREFIX = "refresh:user:";

    public void save(String username, String refreshToken) {
        redisTemplate.opsForValue().set(buildKey(username), refreshToken, REFRESH_TTL);
    }

    public boolean isValid(String username, String refreshToken) {
        String storedToken = redisTemplate.opsForValue().get(buildKey(username));
        return refreshToken != null && refreshToken.equals(storedToken);
    }

    public void delete(String username) {
        redisTemplate.delete(buildKey(username));
    }

    private String buildKey(String username) {
        return KEY_PREFIX + username;
    }
}
