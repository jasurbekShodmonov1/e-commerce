package com.example.e_commerce.redis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessageReceiver {
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public void receiveMessage(String message) {
        log.info("New message received from Redis channel: {}", message);

        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            JsonNode userIdNode = jsonNode.get("userId");
            if (userIdNode == null || userIdNode.isNull()) {
                log.warn("Received message without userId, skipping: {}", message);
                return;
            }
            Long userId = userIdNode.asLong();

            String destination = "/topic/orders/" + userId;
            messagingTemplate.convertAndSend(destination, message);
            log.info("Live notification successfully pushed to WebSocket: {}", destination);
        } catch (Exception e) {
            log.error("Error while pushing message to WebSocket", e);
        }
    }
}
