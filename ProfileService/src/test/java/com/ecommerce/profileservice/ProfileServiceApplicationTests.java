package com.ecommerce.profileservice;

import com.ecommerce.profileservice.dto.UserProfileDTO;
import com.ecommerce.profileservice.entity.User;
import com.ecommerce.profileservice.repository.UserRepository;
import com.ecommerce.profileservice.service.ProfileServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceApplicationTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private User user;
    private UserProfileDTO profileDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setFullName("John Doe");
        user.setEmail("john@example.com");
        user.setAddress("123 Main St");
        user.setPhoneNumber("123-456-7890");
        user.setRole("USER");

        profileDTO = new UserProfileDTO();
        profileDTO.setUserId(1L);
        profileDTO.setFullName("John Updated");
        profileDTO.setAddress("456 Updated St");
        profileDTO.setPhoneNumber("098-765-4321");
    }

    @Test
    void testGetProfile_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        UserProfileDTO result = profileService.getProfile(1L);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
        assertEquals("123 Main St", result.getAddress());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProfile_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            profileService.getProfile(1L);
        });
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateProfile_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserProfileDTO result = profileService.updateProfile(1L, profileDTO);

        // Assert
        assertNotNull(result);
        assertEquals("John Updated", result.getFullName());
        assertEquals("456 Updated St", result.getAddress());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateProfile_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            profileService.updateProfile(1L, profileDTO);
        });
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteProfile_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        profileService.deleteProfile(1L);

        // Assert
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteProfile_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            profileService.deleteProfile(1L);
        });
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).delete(any(User.class));
    }
}