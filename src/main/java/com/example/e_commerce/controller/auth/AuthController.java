package com.example.e_commerce.controller.auth;


import com.example.e_commerce.dto.auth.request.LoginRequest;
import com.example.e_commerce.dto.auth.response.LoginResponse;
import com.example.e_commerce.repository.UserRepository;
import com.example.e_commerce.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);
    }



}
