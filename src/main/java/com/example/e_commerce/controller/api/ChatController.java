package com.example.e_commerce.controller.api;

import com.example.e_commerce.dto.chat.ChatMessageRequest;
import com.example.e_commerce.dto.chat.ChatMessageResponse;
import com.example.e_commerce.security.JwtUtil;
import com.example.e_commerce.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtUtil jwtUtil;

    @GetMapping("/conversation/{userId}")
    public List<ChatMessageResponse> getConversation(@PathVariable Long userId) {
        return chatService.getConversation(userId);
    }

    @PostMapping("/messages")
    public ChatMessageResponse sendByRest(@RequestBody @Valid ChatMessageRequest request) {
        ChatMessageResponse response = chatService.saveFromCurrentUser(request);
        messagingTemplate.convertAndSend(chatService.conversationTopic(response.senderId(), response.recipientId()), response);
        return response;
    }

    @MessageMapping("/chat.send")
    public void sendByWebSocket(@Header("Authorization") String authorization,
                                @Valid ChatMessageRequest request) {
        String token = authorization != null && authorization.startsWith("Bearer ")
                ? authorization.substring(7)
                : authorization;

        if (!jwtUtil.validateToken(token)) {
            return;
        }

        String username = jwtUtil.extractUsername(token);
        ChatMessageResponse response = chatService.saveFromUsername(username, request);
        messagingTemplate.convertAndSend(chatService.conversationTopic(response.senderId(), response.recipientId()), response);
    }
}
