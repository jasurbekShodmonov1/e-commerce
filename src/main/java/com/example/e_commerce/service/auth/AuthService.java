package com.example.e_commerce.service.auth;

import com.example.e_commerce.dto.auth.request.LoginRequest;
import com.example.e_commerce.dto.auth.response.LoginResponse;
import com.example.e_commerce.entity.user.User;
import com.example.e_commerce.exception.PasswordDoesNotMatchException;
import com.example.e_commerce.exception.UserNotFoundException;
import com.example.e_commerce.repository.UserRepository;
import com.example.e_commerce.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public LoginResponse login(LoginRequest loginRequest){
        User user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(()->new UserNotFoundException("User not found with"+loginRequest.username()));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new PasswordDoesNotMatchException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername(),user.getRole());

        log.info("Successfully login");
        return new LoginResponse(
                token,
                user.getUsername(),
                user.getRole()
        );

    }


}
