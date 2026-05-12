package com.example.e_commerce.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderItemResponse(

        @NotNull
        Long productId,

        @NotBlank
        String productName,

        @NotNull
        @Positive
        Integer quantity,

        @NotNull
        @Positive
        BigDecimal unitPrice,

        @NotNull
        @Positive
        BigDecimal totalPrice
) {

}
