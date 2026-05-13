package com.example.e_commerce.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        String body = String.format(
                "{\"timestamp\": \"%s\", \"status\": %d, \"error\": \"Forbidden\", \"message\": \"Access denied: You do not have the necessary permissions\", \"path\": \"%s\"}",
                Instant.now(),
                HttpServletResponse.SC_FORBIDDEN,
                request.getServletPath()
        );

        response.getWriter().write(body);
    }
}
