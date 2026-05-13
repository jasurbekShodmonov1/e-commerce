package com.example.e_commerce.utils;

import com.example.e_commerce.entity.user.User;
import com.example.e_commerce.entity.user.UserRoles;
import com.example.e_commerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

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

                userRepository.save(admin);
                System.out.println("LOG: Admin user has been initialized in the database.");
            } else {
                System.out.println("LOG: Admin user already exists. Skipping initialization.");
            }
        };
    }
}
