package com.example.e_commerce.mapper;

import com.example.e_commerce.dto.request.UserRequest;
import com.example.e_commerce.dto.response.UserResponse;
import com.example.e_commerce.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toEntity(UserRequest userRequest);

    UserResponse toDto(User user);
}
