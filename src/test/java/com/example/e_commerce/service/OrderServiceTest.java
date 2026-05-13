package com.example.e_commerce.service;


import com.example.e_commerce.dto.request.CreateOrderRequest;
import com.example.e_commerce.dto.request.OrderItemRequest;
import com.example.e_commerce.dto.response.OrderItemResponse;
import com.example.e_commerce.dto.response.OrderResponse;
import com.example.e_commerce.entity.order.Order;
import com.example.e_commerce.entity.order.OrderItem;
import com.example.e_commerce.entity.order.OrderStatus;
import com.example.e_commerce.entity.order.Product;
import com.example.e_commerce.exception.InsufficientStockException;
import com.example.e_commerce.exception.InvalidOrderStatusException;
import com.example.e_commerce.exception.OrderNotFoundException;
import com.example.e_commerce.exception.ProductNotFoundException;
import com.example.e_commerce.mapper.OrderMapper;
import com.example.e_commerce.repository.OrderRepository;
import com.example.e_commerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private final Long id = 5L;
    private final String customerName = "Bred";
    private final String customerEmail = "bred@pit";
    private final LocalDateTime orderDate = LocalDateTime.now();
    private final OrderStatus status = OrderStatus.PENDING;
    private final BigDecimal totalAmount = BigDecimal.valueOf(90);

    private final Long productId = 2L;
    private final String productName = "apple";
    private final Integer quantity = 5;
    private final BigDecimal price = BigDecimal.valueOf(2);
    private final BigDecimal totalPrice = BigDecimal.valueOf(10);

    private Order order;
    private OrderItem orderItem;
    private Product product;
    private OrderItemRequest orderItemRequest = new OrderItemRequest(
            productId,
            quantity
    );
    private CreateOrderRequest createOrderRequest = new CreateOrderRequest(
            customerName,
            customerEmail,
            List.of(orderItemRequest)
    );

    private OrderItemResponse itemResponse = new OrderItemResponse(
            productId,
            productName,
            quantity,
            price,
            totalPrice
    );

    private OrderResponse response = new OrderResponse(
            id,
            customerName,
            customerEmail,
            orderDate,
            status,
            totalAmount,
            List.of(itemResponse)
    );

    @BeforeEach
    void setUp(){
        order = new Order();
        order.setCustomerName(customerName);
        order.setCustomerEmail(customerEmail);
        order.setOrderStatus(OrderStatus.PENDING);

        product = new Product();
        product.setId(productId);
        product.setPrice(price);
        product.setName(productName);
        product.setStock(7);

        orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
    }

    @Test
    void getAll_ShouldWork(){
        List<OrderResponse> expect = List.of(response);
        when(orderRepository.findAllWithItems()).thenReturn(List.of(order));
        when(orderMapper.toDto(order)).thenReturn(response);

        List<OrderResponse> actual = orderService.getAll();
        assertEquals(expect,actual);
        verify(orderRepository).findAllWithItems();
        verify(orderMapper).toDto(order);
    }

    @Test
    void getById_ShouldWork(){
        OrderResponse expect = response;
        when(orderRepository.findByIdWithItems(id)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(response);
        OrderResponse actual = orderService.getById(id);
        assertEquals(expect,actual);
        verify(orderRepository).findByIdWithItems(id);
        verify(orderMapper).toDto(order);
    }

    @Test
    void getById_ShouldNotWork(){
        when(orderRepository.findByIdWithItems(id)).thenReturn(Optional.empty());
        assertThrows(
                OrderNotFoundException.class,
                ()->orderService.getById(id)
        );

        verify(orderRepository).findByIdWithItems(id);
        verify(orderMapper, never()).toDto(order);
    }

    @Test
    void createOrder_ShouldWork(){
        OrderResponse expect = response;

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(response);
        OrderResponse actual = orderService.createOrder(createOrderRequest);

        assertEquals(expect,actual);

    }

    @Test
    void createOrder_ShouldThrowProductNotFound(){
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                ()->orderService.createOrder(createOrderRequest)
        );

        verify(productRepository).findById(productId);

    }

    @Test
    void createOrder_ShouldThrowInsufficientNotFoundWhenStockNull(){
        product.setStock(0);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(
                InsufficientStockException.class,
                ()->orderService.createOrder(createOrderRequest)
        );

        verify(productRepository).findById(productId);

    }

    @Test
    void createOrder_ShouldThrowInsufficientNotFound(){
        product.setStock(3);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(
                InsufficientStockException.class,
                ()->orderService.createOrder(createOrderRequest)
        );

        verify(productRepository).findById(productId);

    }

    @Test
    void createOrder_ShouldNotSameProduct(){
        OrderItemRequest itemRequest = new OrderItemRequest(
                productId,
                1
        );

        CreateOrderRequest request = new CreateOrderRequest(
                customerName,
                customerEmail,
                List.of(orderItemRequest,itemRequest)
        );
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(
                IllegalArgumentException.class,
                ()->orderService.createOrder(request)
        );

        verify(productRepository,times(2)).findById(productId);
    }

    @Test
    void deleteOrder_ShouldWork(){
        String expect = "Order cancelled";
        order.setOrderItems(List.of(orderItem));
        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        String actual = orderService.deleteOrder(id);

        assertEquals(expect,actual);
        verify(orderRepository).findById(id);
    }

    @Test
    void deleteOrder_ShouldNotWork(){
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                ()->orderService.deleteOrder(id)
        );

        verify(orderRepository).findById(id);
    }

    @Test
    void changeStatus_ShouldWork(){
        OrderResponse expect = response;
        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(response);
        OrderResponse actual = orderService.changeStatus(id,OrderStatus.SHIPPED);

        assertEquals(expect,actual);

        verify(orderRepository).findById(id);
        verify(orderRepository).save(any());
        verify(orderMapper).toDto(order);
    }

    @Test
    void changeStatus_ShouldWorkWhenStatusPending(){
        OrderResponse expect = response;
        order.setOrderStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        assertThrows(
                InvalidOrderStatusException.class,
                ()->orderService.changeStatus(id,OrderStatus.DELIVERED)
        );

        verify(orderRepository).findById(id);
        verify(orderRepository, never()).save(any());
        verify(orderMapper, never()).toDto(order);

    }

    @Test
    void getByCustomerEmail_ShouldWork(){
        List<OrderResponse> expect = List.of(response);
        when(orderRepository.findByCustomerEmail(customerEmail)).thenReturn(List.of(order));
        when(orderMapper.toDto(order)).thenReturn(response);
        List<OrderResponse> actual = orderService.getByCustomerEmail(customerEmail);

        assertEquals(expect,actual);
        verify(orderRepository).findByCustomerEmail(customerEmail);
        verify(orderMapper).toDto(order);
    }






}
