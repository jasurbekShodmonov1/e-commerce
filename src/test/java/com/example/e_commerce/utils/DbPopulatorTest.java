package com.example.e_commerce.utils;


import com.example.e_commerce.entity.user.User;
import com.example.e_commerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DbPopulatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DbPopulator dbPopulator;

    @Test
    void shouldCreateAdminWhenNotExists() throws Exception {

        String adminUsername = "admin";
        when(userRepository.findByUsername(adminUsername)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("admin123")).thenReturn("encodedPassword");


        CommandLineRunner runner = dbPopulator.initAdmin(userRepository, passwordEncoder);
        runner.run();

        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("admin123");
    }

    @Test
    void shouldNotCreateAdminWhenAlreadyExists() throws Exception {

        String adminUsername = "admin";
        User existingAdmin = new User();
        existingAdmin.setUsername(adminUsername);

        when(userRepository.findByUsername(adminUsername)).thenReturn(Optional.of(existingAdmin));


        CommandLineRunner runner = dbPopulator.initAdmin(userRepository, passwordEncoder);
        runner.run();


        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

}
