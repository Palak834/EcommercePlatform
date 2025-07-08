package com.ecommerce.registrationservice;

import com.ecommerce.registrationservice.entity.User;
import com.ecommerce.registrationservice.repository.UserRepository;
import com.ecommerce.registrationservice.service.RegistrationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceApplicationTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationService registrationService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setPassword("password123");
        user.setRole("USER");
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = registrationService.registerUser(user);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("USER", result.getRole());
        assertEquals("encodedPassword", result.getPassword());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(passwordEncoder, times(1)).encode("password123"); // Corrected verification
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        // Arrange
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            registrationService.registerUser(user);
        });
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_NullRole() {
        // Arrange
        user.setRole(null);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = registrationService.registerUser(user);

        // Assert
        assertNotNull(result);
        assertEquals("USER", result.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }
}