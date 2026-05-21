package com.example.e_commerce.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatMessageRequest(
        @NotNull
        Long recipientId,

        @NotBlank
        String content
) {
}
