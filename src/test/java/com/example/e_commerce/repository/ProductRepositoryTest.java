package com.example.e_commerce.repository;

import com.example.e_commerce.entity.order.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {

        product1 = new Product();
        product1.setName("apple");
        product1.setCategory("fruit");
        product1.setIsActive(true);

        product2 = new Product();
        product2.setName("banana");
        product2.setCategory("fruit");
        product2.setIsActive(true);

        productRepository.saveAll(List.of(product1, product2));
    }

    @Test
    void search_ByName_ShouldReturnMatchingProducts(){
        List<Product> result = productRepository.search("apple",null);
        assertEquals(1, result.size());
        assertEquals("apple", result.get(0).getName());
    }

    @Test
    void search_ByCategory_ShouldReturnMatchingProducts(){
        List<Product> result = productRepository.search(null,"fruit");
        assertEquals(2, result.size());
        assertEquals("fruit", result.get(0).getCategory());
    }

    @Test
    void search_ByNameAndCategory_ShouldReturnFiltered() {

        List<Product> result =
                productRepository.search("apple", "fruit");

        assertEquals(1, result.size());
    }

    @Test
    void search_NoParams_ShouldReturnAllActiveProducts() {

        List<Product> result =
                productRepository.search(null, null);

        assertEquals(2, result.size());
    }

    @Test
    void search_ShouldExcludeInactiveProducts() {

        Product inactive = new Product();
        inactive.setName("hidden");
        inactive.setCategory("fruit");
        inactive.setIsActive(false);

        productRepository.save(inactive);

        List<Product> result =
                productRepository.search(null, "fruit");

        assertEquals(2, result.size());
    }

    @Test
    void search_ShouldBeCaseInsensitive() {

        List<Product> result =
                productRepository.search("APPLE", null);

        assertEquals(1, result.size());
    }


}
