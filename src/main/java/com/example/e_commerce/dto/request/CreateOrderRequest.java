package com.example.e_commerce.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
        @NotBlank(message = "Customer name is required")
        String customerName,

        @Email(message = "Invalid email")
        @NotBlank(message = "Customer email is required")
        String customerEmail,

        @NotEmpty(message = "Order items cannot be empty")
        List<OrderItemRequest> items
) {
}
