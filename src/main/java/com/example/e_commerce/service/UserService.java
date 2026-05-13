package com.example.e_commerce.service;


import com.example.e_commerce.dto.request.UserRequest;
import com.example.e_commerce.dto.response.UserResponse;
import com.example.e_commerce.entity.user.User;
import com.example.e_commerce.entity.user.UserRoles;
import com.example.e_commerce.exception.UserNotFoundException;
import com.example.e_commerce.mapper.UserMapper;
import com.example.e_commerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserRequest userRequest){
        User user = userMapper.toEntity(userRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRoles.USER);
        user.setCreatedAt(LocalDateTime.now());
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    public List<UserResponse> getAllUsers(){
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toDto).toList();
    }

    public UserResponse getUserById(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException("User not found "));

        return userMapper.toDto(user);
    }

    public UserResponse createAdmin(UserRequest adminRequest){
        User admin = userMapper.toEntity(adminRequest);
        admin.setPassword(passwordEncoder.encode(adminRequest.password()));
        admin.setRole(UserRoles.ADMIN);
        admin.setCreatedAt(LocalDateTime.now());
        User adminSaved = userRepository.save(admin);

        return userMapper.toDto(adminSaved);
    }
}
