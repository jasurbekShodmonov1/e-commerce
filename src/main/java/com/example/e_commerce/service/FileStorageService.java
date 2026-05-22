package com.example.e_commerce.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    // 🔥 1. Brauzer tashqaridan ulanishi uchun external-url ni olamiz
    @Value("${minio.external-url:http://localhost:9000}")
    private String minioExternalUrl;

    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID().toString() + "." + extension;

            // Faylni MinIO ichiga yuklash
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 🔥 2. FAOAT fileName EMAS, TO'LIQ URL QAYTARAMIZ:
            // Natija: http://localhost:9000/products-bucket/UUID_nomi.jpg
            return minioExternalUrl + "/" + bucketName + "/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("MinIO fayl omboriga rasm yuklashda xatolik: " + e.getMessage());
        }
    }
}