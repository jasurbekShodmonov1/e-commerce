package com.example.e_commerce.controller.api;

import com.example.e_commerce.dto.request.ProductRequest;
import com.example.e_commerce.dto.response.PageResponse;
import com.example.e_commerce.dto.response.ProductResponse;
import com.example.e_commerce.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        PageResponse<ProductResponse> productResponses = productService.getAllProducts(page, size);
        return ResponseEntity.ok(productResponses);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("productId") Long id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createProduct(@Valid @ModelAttribute  ProductRequest productRequest){
        MultipartFile image = productRequest.image();

        return ResponseEntity.ok(productService.createProduct(productRequest,image));
    }

    @PutMapping(value = "/{productId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable("productId") Long id,
                                                         @Valid @ModelAttribute ProductRequest productRequest){
        MultipartFile image = productRequest.image();
        return ResponseEntity.ok(productService.updateProduct(id,productRequest,image));
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
