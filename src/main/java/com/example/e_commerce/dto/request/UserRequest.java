package com.example.e_commerce.dto.request;

import com.example.e_commerce.entity.user.UserRoles;

public record UserRequest(
        String fullName,
        String username,
        String password
) {
}
