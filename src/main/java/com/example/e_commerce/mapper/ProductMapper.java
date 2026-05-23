package com.example.e_commerce.mapper;

import com.example.e_commerce.dto.request.ProductRequest;
import com.example.e_commerce.dto.response.ProductResponse;
import com.example.e_commerce.entity.order.Product;
import org.springframework.beans.factory.annotation.Value;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ProductMapper {

    @Value("${minio.external-url:http://localhost:9000}")
    private String minioExternalUrl;

    @Value("${minio.bucket.name}")
    private String bucketName;

    public abstract Product toEntity(ProductRequest productRequest);

    @Mapping(target = "imageUrl", expression = "java(toImageUrl(product.getImageName(), true))")
    public abstract ProductResponse toDto(Product product);

    public abstract void updateFromDto(ProductRequest productRequest, @MappingTarget Product product);

    protected String toImageUrl(String imageName, boolean unused) {
        if (imageName == null || imageName.isBlank()) {
            return null;
        }

        if (imageName.startsWith("http://") || imageName.startsWith("https://")) {
            return imageName;
        }

        return minioExternalUrl.replaceAll("/+$", "") + "/" + bucketName + "/" + imageName;
    }
}
