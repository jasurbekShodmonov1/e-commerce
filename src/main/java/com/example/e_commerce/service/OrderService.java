package com.example.e_commerce.service;


import com.example.e_commerce.dto.event.OrderStatusChangedEvent;
import com.example.e_commerce.dto.request.CreateOrderRequest;
import com.example.e_commerce.dto.request.OrderItemRequest;
import com.example.e_commerce.dto.response.OrderResponse;
import com.example.e_commerce.entity.order.Order;
import com.example.e_commerce.entity.order.OrderItem;
import com.example.e_commerce.entity.order.OrderStatus;
import com.example.e_commerce.entity.order.Product;
import com.example.e_commerce.entity.user.User;
import com.example.e_commerce.exception.InsufficientStockException;
import com.example.e_commerce.exception.InvalidOrderStatusException;
import com.example.e_commerce.exception.OrderNotFoundException;
import com.example.e_commerce.exception.ProductNotFoundException;
import com.example.e_commerce.mapper.OrderMapper;
import com.example.e_commerce.repository.OrderRepository;
import com.example.e_commerce.repository.ProductRepository;
import com.example.e_commerce.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final ApplicationEventPublisher eventPublisher;

    public List<OrderResponse> getAll(){
        List<Order> orders = orderRepository.findAllWithItems();

        return orders.stream().map(orderMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {

        Order order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found"));

        return orderMapper.toDto(order);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public OrderResponse createOrder(CreateOrderRequest createOrderRequest){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        log.info("Creating order for customer: {}",
                createOrderRequest.customerName());
        Order order = new Order();

        order.setCustomerName(createOrderRequest.customerName());
        order.setCustomerEmail(createOrderRequest.customerEmail());
        order.setOrderDate(LocalDateTime.now());
        order.setUser(user);
        order.setOrderStatus(OrderStatus.PENDING);
        List<OrderItem> orderItems = new ArrayList<>();

        Set<Long> productIds = new HashSet<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest orderItemRequest:createOrderRequest.items()){
            log.info("Processing product id: {}",
                    orderItemRequest.productId());

            Product product = productRepository.findById(orderItemRequest.productId())
                    .orElseThrow(()-> {
                        log.error("Product not found: {}",
                                orderItemRequest.productId());

                        return new ProductNotFoundException(
                                "Product not found"
                        );
                    });

            log.info("Product found: {}",
                    product.getName());

            if(product.getStock()== 0 ){
                throw new InsufficientStockException(
                        "Product is out of stock: " + product.getName()
                );
            }
            if(product.getStock() < orderItemRequest.quantity()){
                throw new InsufficientStockException("Not enough stock for product: "
                        + product.getName());
            }

            if (!productIds.add(orderItemRequest.productId())) {

                throw new IllegalArgumentException(
                        "Product already exists in order"
                );
            }

            BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(orderItemRequest.quantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(orderItemRequest.quantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setTotalPrice(totalPrice);

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(totalPrice);

            product.setStock(
                    product.getStock()
                            - orderItemRequest.quantity()
            );

            productRepository.save(product);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order save = orderRepository.save(order);
        log.info("Order created successfully");

        return orderMapper.toDto(save);
    }


    @Transactional
    public String deleteOrder(Long id){
        log.info("delete order start");
        Order order = orderRepository.findById(id)
                .orElseThrow(()->new OrderNotFoundException("Order not found"));

        for (OrderItem orderItem: order.getOrderItems()){
            Product product = orderItem.getProduct();

            product.setStock(
                    product.getStock() + orderItem.getQuantity()
            );

            productRepository.save(product);
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.delete(order);
        log.info("order deleted successfully");

        return "Order cancelled";
    }

    @Transactional
    public OrderResponse changeStatus(Long orderId, OrderStatus status){
        log.info("changing of the order's status starts");
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException(
                    "Only PENDING orders can be updated"
            );
        }

        order.setOrderStatus(status);
        orderRepository.save(order);
        log.info("status of order changed successfully");

        eventPublisher.publishEvent(new OrderStatusChangedEvent(
                order.getId(),
                order.getUser().getUserId(),
                status
                ));

        return orderMapper.toDto(order);
    }

    public List<OrderResponse> getByCustomerEmail(String email){
        List<Order> orders =
                orderRepository.findByCustomerEmail(email);

        return orders.stream()
                .map(orderMapper::toDto)
                .toList();
    }
}
