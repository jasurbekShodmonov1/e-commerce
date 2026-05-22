package com.example.e_commerce.entity.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_seq_gen")
    @SequenceGenerator(
            name = "products_seq_gen",
            sequenceName = "products_seq",
            allocationSize = 50
    )
    private Long id;

    private String name;
    private BigDecimal price;
    @Column(name = "image_name")
    private String imageName;
    private Integer stock;
    private String category;
    private Boolean isActive;
    private LocalDateTime createdAt;

}
