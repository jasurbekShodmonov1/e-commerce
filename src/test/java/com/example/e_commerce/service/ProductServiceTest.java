package com.example.e_commerce.service;

import com.example.e_commerce.dto.request.ProductRequest;
import com.example.e_commerce.dto.response.PageResponse;
import com.example.e_commerce.dto.response.ProductResponse;
import com.example.e_commerce.entity.order.Product;
import com.example.e_commerce.exception.ProductNotFoundException;
import com.example.e_commerce.mapper.ProductMapper;
import com.example.e_commerce.repository.ProductRepository;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ProductService productService;

    private final Long id = 5L;
    private final String name = "apple";
    private final BigDecimal price = BigDecimal.valueOf(50);
    private final Integer stock = 10;
    private final Boolean isActive = true;
    private final String category = "fruit";
    private  Product product;

    MockMultipartFile mockImage = new MockMultipartFile(
            "image",
            "test-image.png",
            "image/png",
            "rasm_baytlari".getBytes()
    );
    private @NotNull String imageUrl;
    private ProductResponse response = new ProductResponse(
            id,
            name,
            price,
            stock,
            imageUrl,
            isActive,
            category
    );

    private ProductRequest request = new ProductRequest(
            name,
            price,
            stock,
            category,
            mockImage
    );


    @BeforeEach
    void setUp(){
        product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
    }

    @Test
    void getAllProducts_ShouldWork(){
        int page = 0;
        int size = 1;
        List<Product> products = List.of(product);

        Page<Product> productPage = new PageImpl<>(
                products,
                PageRequest.of(page, size),
                products.size()
        );

        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);
        when(productMapper.toDto(product)).thenReturn(response);
        PageResponse<ProductResponse> result =
                productService.getAllProducts(page, size);

        assertNotNull(result);

        assertEquals(1, result.content().size());

    }

    @Test
    void getProductById_ShouldWork(){
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(response);

        ProductResponse actual = productService.getProductById(id);

        assertEquals(name, actual.name());
        assertEquals(price, actual.price());

        verify(productRepository).findById(any());
        verify(productMapper).toDto(any());
    }

    @Test
    void getProductByIdThrowProductNotFound(){
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        ProductNotFoundException exception = assertThrows(
            ProductNotFoundException.class,
                ()->productService.getProductById(id)
        );

        verify(productRepository).findById(any());
        verify(productMapper, never()).toDto(any());
    }

    @Test
    void createProduct_ShouldWork(){
        ProductResponse expect = response;

        when(fileStorageService.uploadFile(mockImage)).thenReturn("unikal-fayl-nomi.png");
        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.save(product))
                .thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(response);

        ProductResponse actual = productService.createProduct(request,mockImage);

        assertEquals(expect,actual);
        verify(productMapper).toEntity(any());
        verify(productMapper).toDto(any());
    }

    @Test
    void updateProduct_ShouldWork(){
        ProductResponse expect = response;

        when(fileStorageService.uploadFile(mockImage)).thenReturn("unikal-fayl-nomi.png");
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(product))
                .thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(response);

        ProductResponse actual = productService.updateProduct(id,request,mockImage);

        assertEquals(expect,actual);
        verify(productRepository).findById(any());
        verify(productMapper).toDto(any());
    }

    @Test
    void updateProduct_ShouldNotWork(){
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                ()->productService.updateProduct(id,request,mockImage)
        );

        verify(productRepository).findById(any());
        verify(productMapper, never()).toDto(any());
    }

    @Test
    void deleteProduct_ShouldNotWork(){
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                ()->productService.deleteProduct(id)
        );

        verify(productRepository).findById(any());
    }

    @Test
    void deleteProduct_ShouldWork(){
        String expect = "Product deleted successfully";
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        String actual = productService.deleteProduct(id);
        assertEquals(expect,actual);
        verify(productRepository).findById(any());
    }

    @Test
    void search_ShouldWork(){
        List<ProductResponse> expect= List.of(response);
        when(productRepository.search(name, category)).thenReturn(List.of(product));
        when(productMapper.toDto(product)).thenReturn(response);

        List<ProductResponse> actual = productService.search(name,category);

        assertEquals(expect, actual);

        verify(productRepository).search(name,category);
        verify(productMapper).toDto(product);
    }




}
