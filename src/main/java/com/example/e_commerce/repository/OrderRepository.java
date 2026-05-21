package com.example.e_commerce.repository;

import com.example.e_commerce.entity.order.Order;
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
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    @Query("""
    select o from Order o
    join fetch o.orderItems oi
    join fetch oi.product
    where o.customerEmail = :email
""")
    List<Order> findByCustomerEmail(@Param("email") String email);

    @Query("""
    select distinct o from Order o
    join fetch o.orderItems oi
    join fetch oi.product
    join o.user u
    where u.username = :username
    order by o.orderDate desc
""")
    List<Order> findByUsernameWithItems(@Param("username") String username);
}
