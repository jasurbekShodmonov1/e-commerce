package com.example.e_commerce.service;


import com.example.e_commerce.dto.request.UserRequest;
import com.example.e_commerce.dto.response.UserResponse;
import com.example.e_commerce.entity.user.User;
import com.example.e_commerce.entity.user.UserRoles;
import com.example.e_commerce.exception.UserNotFoundException;
import com.example.e_commerce.mapper.UserMapper;
import com.example.e_commerce.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private final Long id = 5L;
    private final String fullName = "Bred Pit";
    private final String username = "bred";
    private final String password = "bred";
    private final LocalDateTime createdAt = LocalDateTime.now();

    private User user;
    private UserRequest request = new UserRequest(
            fullName,
            username,
            password
    );

    private UserResponse response(UserRoles role) {
        return new UserResponse(
            id,
            fullName,
            username,
            role,
            createdAt
        );
    }

    @BeforeEach
    void setUp(){
        user = new User();
        user.setUserId(id);
        user.setFullName(fullName);
        user.setUsername(username);
        user.setPassword(password);
    }

    @Test
    void createUser_ShouldWork(){
        UserResponse expect = response(UserRoles.USER);
        user.setRole(UserRoles.USER);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(response(UserRoles.USER));
        UserResponse actual = userService.createUser(request);
        assertEquals(expect,actual);

        verify(userMapper).toEntity(request);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(user);
    }

    @Test
    void getAllUsers_ShouldWork(){
        List<UserResponse> expect = List.of(response(UserRoles.USER));

        user.setRole(UserRoles.USER);
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(response(UserRoles.USER));

        List<UserResponse> actual = userService.getAllUsers();
        assertEquals(expect,actual);

        verify(userRepository).findAll();
        verify(userMapper).toDto(any(User.class));
    }

    @Test
    void getUserById_ShouldWork(){
        UserResponse expect = response(UserRoles.ADMIN);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(response(UserRoles.ADMIN));
        UserResponse actual = userService.getUserById(id);
        assertEquals(expect,actual);

        verify(userRepository).findById(id);
        verify(userMapper).toDto(any(User.class));

    }

    @Test
    void getUserById_ShouldNotWork(){
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                ()->userService.getUserById(id)
        );

        verify(userRepository).findById(id);
        verify(userMapper, never()).toDto(any(User.class));

    }

    @Test
    void createAdmin_ShouldWork(){
        UserResponse expect = response(UserRoles.ADMIN);
        user.setRole(UserRoles.ADMIN);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(response(UserRoles.ADMIN));
        UserResponse actual = userService.createAdmin(request);
        assertEquals(expect,actual);

        verify(userMapper).toEntity(request);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(user);
    }


}
