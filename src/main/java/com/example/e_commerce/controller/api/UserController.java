package com.example.e_commerce.controller.api;

import com.example.e_commerce.dto.request.UserRequest;
import com.example.e_commerce.dto.response.UserResponse;
import com.example.e_commerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponse> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserResponse getByUserId(@PathVariable Long userId){
        return userService.getUserById(userId);
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(){
        return userService.getCurrentUser();
    }

    @GetMapping("/chat")
    public List<UserResponse> getChatUsers(){
        return userService.getChatUsers();
    }

    @PostMapping("/register")
    public UserResponse registerUser(@RequestBody UserRequest userRequest){
        return userService.createUser(userRequest);
    }

    @PostMapping("/createAdmin")
    public UserResponse createAdmin(@RequestBody UserRequest adminRequest){
        return userService.createAdmin(adminRequest);
    }
}
