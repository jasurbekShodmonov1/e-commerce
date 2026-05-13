package com.example.e_commerce.dto.response;

import com.example.e_commerce.entity.user.UserRoles;

import java.time.LocalDateTime;

public record UserResponse (
    Long userId,
    String  fullName,
    String username,
    UserRoles role,
    LocalDateTime createdAt
){
}
