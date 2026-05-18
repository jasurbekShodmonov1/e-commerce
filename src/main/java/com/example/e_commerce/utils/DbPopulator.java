package com.example.e_commerce.utils;

import com.example.e_commerce.entity.user.User;
import com.example.e_commerce.entity.user.UserRoles;
import com.example.e_commerce.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Slf4j
@Configuration
public class DbPopulator {

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminUsername = "admin";

            if (userRepository.findByUsername(adminUsername).isEmpty()) {
                User admin = new User();
                admin.setUsername(adminUsername);

                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(UserRoles.ADMIN);
                admin.setCreatedAt(LocalDateTime.now());

                userRepository.save(admin);
                log.info(" Admin user has been initialized in the database.");
            } else {
                log.info("LOG: Admin user already exists. Skipping initialization.");
            }
        };
    }
}
