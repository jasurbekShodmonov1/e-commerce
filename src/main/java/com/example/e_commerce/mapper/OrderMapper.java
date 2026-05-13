package com.example.e_commerce.mapper;


import com.example.e_commerce.dto.response.OrderResponse;
import com.example.e_commerce.entity.order.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = OrderItemMapper.class)
public interface OrderMapper {

    @Mapping(source = "orderStatus", target = "status")
    @Mapping(source = "orderItems", target = "items")
    OrderResponse toDto(Order order);
}
