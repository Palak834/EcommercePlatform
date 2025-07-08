package com.ecommerce.loginservice;

import com.ecommerce.loginservice.entity.User;
import com.ecommerce.loginservice.repository.UserRepository;
import com.ecommerce.loginservice.service.LoginService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

@ExtendWith(MockitoExtension.class)
class LoginServiceApplicationTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginService loginService;

    private User user;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        user = new User();
        user.setUserId(1L);
        user.setEmail("test@example.com");
        user.setPassword("$2a$10$X7..."); // Hashed password example
        user.setRole("USER");

        // Mock jwtSecret using reflection since it's a private field
        Field jwtSecretField = LoginService.class.getDeclaredField("jwtSecret");
        jwtSecretField.setAccessible(true);
        jwtSecretField.set(loginService, "az19urDV3Ukf9srVBxvZxftj3PE2pgAKTGi4EOS5ObI=");
    }

    @Test
    void testLoginUser_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);

        // Act
        String token = loginService.loginUser("test@example.com", "password123");

        // Assert
        assertNotNull(token);
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("password123", user.getPassword());
    }

    @Test
    void testLoginUser_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            loginService.loginUser("test@example.com", "password123");
        });
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testLoginUser_InvalidPassword() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(passwordEncoder.matches("wrongpass", user.getPassword())).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            loginService.loginUser("test@example.com", "wrongpass");
        });
        assertEquals("Invalid password", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("wrongpass", user.getPassword());
    }
}