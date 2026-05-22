package com.example.e_commerce.mapper;

import com.example.e_commerce.dto.request.ProductRequest;
import com.example.e_commerce.dto.response.ProductResponse;
import com.example.e_commerce.entity.order.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    String MINIO_BASE_URL = "http://localhost:9000/products-bucket/";

    Product toEntity(ProductRequest productRequest);

    @Mapping(target = "imageUrl", expression = "java(product.getImageName() != null ? MINIO_BASE_URL + product.getImageName() : null)")
    ProductResponse toDto(Product product);

    void updateFromDto(ProductRequest productRequest, @MappingTarget Product product);
}
