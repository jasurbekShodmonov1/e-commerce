package com.example.e_commerce.service;

import com.example.e_commerce.dto.request.ProductRequest;
import com.example.e_commerce.dto.response.PageResponse;
import com.example.e_commerce.dto.response.ProductResponse;
import com.example.e_commerce.entity.order.Product;
import com.example.e_commerce.exception.ProductNotFoundException;
import com.example.e_commerce.mapper.ProductMapper;
import com.example.e_commerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public PageResponse<ProductResponse> getAllProducts(int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productPage  = productRepository.findAll(pageable);
        return new PageResponse<>(
                productPage.map(productMapper::toDto).getContent(),
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages()
        );
    }
    public ProductResponse getProductById(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new ProductNotFoundException("Product not found"));

        return productMapper.toDto(product);
    }

    public ProductResponse createProduct(ProductRequest productRequest){
        log.info("creating product start");
        Product product = productMapper.toEntity(productRequest);
        product.setIsActive(true);
        product.setCreatedAt(LocalDateTime.now());
        Product save = productRepository.save(product);
        log.info("product created successfully");
        return productMapper.toDto(save);
    }

    public ProductResponse updateProduct(Long id,ProductRequest productRequest){
        log.info("updating product start");
        Product product = productRepository.findById(id)
                .orElseThrow(()->new ProductNotFoundException("Product not found"));
        productMapper.updateFromDto(productRequest,product);

        Product saved = productRepository.save(product);
        log.info("product updated successfully");
        return productMapper.toDto(saved);

    }

    public String  deleteProduct(Long id){
        log.info("deleting product start");
        Product product = productRepository.findById(id)
                .orElseThrow(()->new ProductNotFoundException("Product not found"));

        productRepository.delete(product);
        log.info("product deleted successfully");
        return "Product deleted successfully";
    }

    public List<ProductResponse> search(String name, String category){

        List<Product> products =
                productRepository.search(name, category);

        return products.stream()
                .map(productMapper::toDto)
                .toList();
    }


}
