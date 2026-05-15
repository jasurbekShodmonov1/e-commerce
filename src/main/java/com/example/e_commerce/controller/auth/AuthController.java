package com.example.e_commerce.controller.auth;


import com.example.e_commerce.dto.auth.request.LoginRequest;
import com.example.e_commerce.dto.auth.request.RefreshTokenRequest;
import com.example.e_commerce.dto.auth.response.LoginResponse;
import com.example.e_commerce.dto.auth.response.TokenResponse;
import com.example.e_commerce.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestBody RefreshTokenRequest request) {
        return authService.refresh(request.refreshToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

}
