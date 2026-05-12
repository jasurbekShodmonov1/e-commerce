package com.example.e_commerce.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductResponse(
        @NotNull
        Long id,

        @NotBlank
        String name,

        @NotNull
        @Positive
        BigDecimal price,

        @NotNull
        @Positive
        Integer stock,

        Boolean isActive,

        @NotBlank
        String category
) {
}
