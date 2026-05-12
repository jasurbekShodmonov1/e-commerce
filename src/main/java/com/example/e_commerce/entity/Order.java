package com.example.e_commerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String customerName;
    private String customerEmail;
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order",
    cascade = CascadeType.ALL,
    orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
}
