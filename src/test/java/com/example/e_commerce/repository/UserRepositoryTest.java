package com.example.e_commerce.repository;

import com.example.e_commerce.entity.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp(){
        user1 = new User();
        user1.setUsername("john");
        user1.setPassword("john");

        user2 = new User();
        user2.setUsername("harry");
        user2.setPassword("harry");

        userRepository.saveAll(List.of(user1,user2));

    }

    @Test
    void findByUsername_ShouldReturnExistingUsername(){
        Optional<User> result = userRepository.findByUsername("john");

        assertEquals("john",result.get().getUsername());
    }

    @Test
    void findByUsername_ShouldReturnFoundNotUsername(){
        Optional<User> result = userRepository.findByUsername("tesla");

        assertTrue(result.isEmpty());
    }
}
