package com.example.e_commerce.mapper;

import com.example.e_commerce.dto.response.OrderItemResponse;
import com.example.e_commerce.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")

    OrderItemResponse toDto(OrderItem item);
}
