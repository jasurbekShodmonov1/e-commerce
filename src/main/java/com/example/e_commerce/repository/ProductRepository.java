package com.example.e_commerce.repository;

import com.example.e_commerce.entity.order.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    @Query("""
    select p from Product p
              where
              (:name is null or lower(p.name) like lower(concat('%', :name, '%')))
              and
              (:category is null or lower(p.category) = lower(:category))
              and
              p.isActive = true 
"""
    )
    List<Product> search(
            @Param("name") String name,
            @Param("category") String category
    );
}
