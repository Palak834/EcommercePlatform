package com.ecommerce.profileservice.service;

import com.ecommerce.profileservice.dto.UserProfileDTO;

// Service interface for profile operations
public interface ProfileService {
    // Retrieve user profile by ID
    UserProfileDTO getProfile(Long userId);
    // Update user profile
    UserProfileDTO updateProfile(Long userId, UserProfileDTO profileDTO);
    // Delete user profile
    void deleteProfile(Long userId);
}