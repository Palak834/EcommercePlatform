package com.ecommerce.profileservice.service;

import com.ecommerce.profileservice.dto.UserProfileDTO;
import com.ecommerce.profileservice.entity.User;
import com.ecommerce.profileservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Implementation of ProfileService
@Service
public class ProfileServiceImpl implements ProfileService {
    // Logger for service operations
    private static final Logger logger = LoggerFactory.getLogger(ProfileServiceImpl.class);

    // Inject UserRepository
    @Autowired
    private UserRepository userRepository;

    // Retrieve user profile by ID
    @Override
    public UserProfileDTO getProfile(Long userId) {
        logger.info("Fetching profile for userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }
    
    // Update user profile
    @Override
    public UserProfileDTO updateProfile(Long userId, UserProfileDTO profileDTO) {
        logger.info("Updating profile for userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFullName(profileDTO.getFullName());
        user.setAddress(profileDTO.getAddress());
        user.setPhoneNumber(profileDTO.getPhoneNumber());
        User updatedUser = userRepository.save(user);
        logger.info("Profile updated successfully for userId: {}", userId);
        return convertToDTO(updatedUser);
    }

    // Delete user profile
    @Override
    public void deleteProfile(Long userId) {
        logger.info("Deleting profile for userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
        logger.info("Profile deleted successfully for userId: {}", userId);
    }

    // Convert User entity to DTO
    private UserProfileDTO convertToDTO(User user) {
        return new UserProfileDTO(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getRole()
        );
    }
}