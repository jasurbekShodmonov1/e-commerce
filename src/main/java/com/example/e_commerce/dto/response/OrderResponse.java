package com.example.e_commerce.dto.response;

import com.example.e_commerce.entity.order.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(

        @NotNull
        Long id,

        @NotBlank
        String customerName,

        @Email
        @NotBlank
        String customerEmail,

        @NotNull
        LocalDateTime orderDate,

        @NotNull
        OrderStatus status,

        @NotNull
        @Positive
        BigDecimal totalAmount,

        @NotEmpty
        List<@Valid OrderItemResponse> items
) {
}
