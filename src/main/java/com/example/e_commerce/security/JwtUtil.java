package com.example.e_commerce.security;

import com.example.e_commerce.entity.user.UserRoles;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class JwtUtil {
    private final String SECRET_STRING = "z58pL9V2xR8vN7qW3mK4jB1hG6fD9sS2aA5zX8cC1vB7nM9lK0jH2gF5dS8aA3zX";
    private final SecretKey secret = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));
    private final long accessTokenExpirationMs = 1000 * 60 * 15;
    private final long refreshTokenExpirationMs = 1000L * 60 * 60 * 24 * 7;

    public String generateAccessToken(String username, UserRoles roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles",roles);
        return buildToken(username, claims, accessTokenExpirationMs);
    }

    public String generateRefreshToken(String username) {
        return buildToken(username, new HashMap<>(), refreshTokenExpirationMs);
    }

    private String buildToken(String username, Map<String, Object> claims, long expirationMs) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secret)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token muddati tugagan: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Noto'g'ri JWT formati");
        } catch (SecurityException e) {
            System.out.println("Token imzosi xato");
        } catch (Exception e) {
            System.out.println("Tokenni tekshirishda xatolik");
        }
        return false;
    }
}
