package com.example.e_commerce.service.auth;

import com.example.e_commerce.dto.auth.request.LoginRequest;
import com.example.e_commerce.dto.auth.response.LoginResponse;
import com.example.e_commerce.dto.auth.response.TokenResponse;
import com.example.e_commerce.entity.user.User;
import com.example.e_commerce.exception.PasswordDoesNotMatchException;
import com.example.e_commerce.exception.UserNotFoundException;
import com.example.e_commerce.repository.UserRepository;
import com.example.e_commerce.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;


    public LoginResponse login(LoginRequest loginRequest){
        User user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(()->new UserNotFoundException("User not found with"+loginRequest.username()));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new PasswordDoesNotMatchException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        refreshTokenService.save(user.getUsername(), refreshToken);

        log.info("Successfully login");
        return new LoginResponse(
                accessToken,
                refreshToken,
                user.getUsername(),
                user.getRole()
        );

    }

    public TokenResponse refresh(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with " + username));

        if (!refreshTokenService.isValid(username, refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is not active");
        }

        String newAccessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        refreshTokenService.save(user.getUsername(), newRefreshToken);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    public void logout(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            return;
        }

        String username = jwtUtil.extractUsername(refreshToken);
        refreshTokenService.delete(username);
    }

}
