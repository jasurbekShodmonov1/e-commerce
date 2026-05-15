package com.example.e_commerce.dto.auth.response;

import com.example.e_commerce.entity.user.UserRoles;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String username,
        UserRoles role
) {
}
