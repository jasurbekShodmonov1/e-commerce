package com.example.e_commerce.dto.response;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        int status,
        LocalDateTime timestamp
) {
}
