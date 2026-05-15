package com.example.e_commerce.dto.auth.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
