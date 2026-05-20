package com.example.e_commerce.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Java 8 Date/Time (LocalDateTime, Instant) xatolarini oldini olish uchun
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
