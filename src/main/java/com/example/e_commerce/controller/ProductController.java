package com.example.e_commerce.controller;

import com.example.e_commerce.dto.request.ProductRequest;
import com.example.e_commerce.dto.response.ProductResponse;
import com.example.e_commerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(){
        List<ProductResponse> productResponses = productService.getAllProducts();
        return ResponseEntity.ok(productResponses);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("productId") Long id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest){
        return ResponseEntity.ok(productService.createProduct(productRequest));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable("productId") Long id, @RequestBody ProductRequest productRequest){
        return ResponseEntity.ok(productService.updateProduct(id,productRequest));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable("productId") Long id){
        return ResponseEntity.ok(productService.deleteProduct(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(
            @RequestParam(required = false)
            String name,

            @RequestParam(required = false)
            String category
    ){
        return ResponseEntity.ok(productService.search(name,category));
    }
}
