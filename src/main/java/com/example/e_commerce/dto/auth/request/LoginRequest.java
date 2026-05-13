package com.example.e_commerce.dto.auth.request;

public record LoginRequest(
        String username,
        String password
) {
}
