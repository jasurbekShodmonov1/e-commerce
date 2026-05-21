package com.example.e_commerce.controller.api;


import com.example.e_commerce.dto.request.CreateOrderRequest;
import com.example.e_commerce.dto.response.OrderResponse;
import com.example.e_commerce.entity.order.OrderStatus;
import com.example.e_commerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderResponse> getAll(){
        return orderService.getAll();
    }

    @GetMapping("/my")
    public List<OrderResponse> getMyOrders(){
        return orderService.getMyOrders();
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrderById(@PathVariable("orderId") Long id){
        return orderService.getById(id);
    }
    @PostMapping
    public OrderResponse create(@RequestBody @Valid CreateOrderRequest orderRequest){
        return orderService.createOrder(orderRequest);
    }

    @PutMapping("/{orderId}/status")
    public OrderResponse updateStatus(@PathVariable Long orderId,
                                         @RequestParam OrderStatus status) {
        return orderService.changeStatus(orderId, status);
    }

    @DeleteMapping("/{orderId}")
    public String delete(@PathVariable("orderId") Long id){
        return orderService.deleteOrder(id);
    }

    @GetMapping("/customer/{email}")
    public List<OrderResponse> getByCustomerEmail(@PathVariable String email){
        return orderService.getByCustomerEmail(email);
    }
}
