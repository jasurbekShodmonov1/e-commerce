package com.example.e_commerce.repository;

import com.example.e_commerce.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long>{
    @Query("""
    select o from Order o
    join fetch o.orderItems oi
    join fetch oi.product
""")
    List<Order> findAllWithItems();

    @Query("""
    select o from Order o
    join fetch o.orderItems oi
    join fetch oi.product
    where o.id = :id
""")
    Optional<Order> findByIdWithItems(Long id);

    @Query("""
    select o from Order o
    join fetch o.orderItems oi
    join fetch oi.product
    where o.customerEmail = :email
""")
    List<Order> findByCustomerEmail(@Param("email") String email);
}
