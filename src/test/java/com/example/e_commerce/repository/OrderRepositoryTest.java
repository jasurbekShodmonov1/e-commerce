package com.example.e_commerce.repository;

import com.example.e_commerce.entity.order.Order;
import com.example.e_commerce.entity.order.OrderItem;
import com.example.e_commerce.entity.order.OrderStatus;
import com.example.e_commerce.entity.order.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@ActiveProfiles("test")
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Product product;
    private Order order;
    private OrderItem orderItem;

    @BeforeEach
    void setup() {

        product = new Product();
        product.setName("TV");
        product.setPrice(BigDecimal.valueOf(1000));
        product.setStock(10);
        product.setIsActive(true);
        testEntityManager.persist(product);

        order = new Order();
        order.setCustomerEmail("test@mail.com");
        order.setCustomerName("Test");
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        testEntityManager.persist(order);

        orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setUnitPrice(product.getPrice());
        orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(2)));
        testEntityManager.persist(orderItem);

        order.setOrderItems(List.of(orderItem));

        testEntityManager.flush();
        testEntityManager.clear();
    }

    @Test
    void findByIdWithItems_ShouldWork() {

        Order result = orderRepository.findByIdWithItems(order.getId())
                .orElseThrow();

        assertEquals(1, result.getOrderItems().size());
        assertEquals("TV", result.getOrderItems().get(0).getProduct().getName());
    }

    @Test
    void findByCustomerEmail_ShouldWork() {

        List<Order> result = orderRepository.findByCustomerEmail("test@mail.com");

        assertEquals(1, result.size());
    }

    @Test
    void findAllWithItems_ShouldWork() {

        List<Order> result = orderRepository.findAllWithItems();

        assertFalse(result.isEmpty());
    }

}
