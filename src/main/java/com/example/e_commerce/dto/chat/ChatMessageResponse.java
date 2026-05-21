package com.example.e_commerce.dto.chat;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long id,
        Long senderId,
        String senderUsername,
        Long recipientId,
        String recipientUsername,
        String content,
        LocalDateTime timestamp
) {
}
