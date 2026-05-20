package com.example.e_commerce.listener;

import com.example.e_commerce.config.RedisPubSubConfig;
import com.example.e_commerce.dto.event.OrderStatusChangedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderNotificationListener {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderStatusChangedEvent(OrderStatusChangedEvent event) {
        log.info("Transaction committed. Handling event for order: {}", event.orderId());

        try {
            String orderMessage = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(RedisPubSubConfig.ORDER_STATUS_CHANNEL, orderMessage);
            log.info("Published order status change to Redis channel '{}': {}",
                    RedisPubSubConfig.ORDER_STATUS_CHANNEL, orderMessage);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize OrderStatusChangedEvent for order {}", event.orderId(), e);
        }
    }
}
