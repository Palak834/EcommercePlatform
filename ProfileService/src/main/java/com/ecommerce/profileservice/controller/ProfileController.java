package com.ecommerce.profileservice.controller;

import com.ecommerce.profileservice.dto.UserProfileDTO;
import com.ecommerce.profileservice.entity.User;
import com.ecommerce.profileservice.repository.UserRepository;
import com.ecommerce.profileservice.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

//REST controller to handle HTTP requests for ProfileService
@RestController
@RequestMapping("/profile")
public class ProfileController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    
    // Inject ProfileService for profile operations
    @Autowired
    private ProfileService profileService;

    // Inject UserRepository for user data access
    @Autowired
    private UserRepository userRepository;

    // Retrieve the authenticated user's profile
    @GetMapping
    public ResponseEntity<UserProfileDTO> getProfile() {
    	// Get email from security context
    	String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Received request to get profile for email: {}", email);

        // Find user by email or throw exception
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
        Long userId = user.getUserId();
        // Fetch profile using ProfileService
        UserProfileDTO profile = profileService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    // Update the authenticated user's profile
    @PutMapping
    public ResponseEntity<UserProfileDTO> updateProfile(@RequestBody UserProfileDTO profileDTO) {
    	// Get email from security context
    	String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Received request to update profile for email: {}", email);

        // Find user by email or throw exception
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
        Long userId = user.getUserId();
        // Update profile using ProfileService
        UserProfileDTO updatedProfile = profileService.updateProfile(userId, profileDTO);
        return ResponseEntity.ok(updatedProfile);
    }

    // Delete the authenticated user's profile 
    @DeleteMapping
    public ResponseEntity<String> deleteProfile() {
    	// Get email from security context
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Received request to delete profile for email: {}", email);

        // Find user by email or throw exception
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
        Long userId = user.getUserId();
        
        // Check if the user has the ADMIN role; if not, they can only delete their own profile
        String userRole = user.getRole();
        String authenticatedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!"ADMIN".equals(userRole) && !email.equals(authenticatedEmail)) {
            logger.warn("User {} attempted to delete another user's profile", email);
            throw new RuntimeException("Unauthorized: Cannot delete another user's profile");
        }

        // Delete profile using ProfileService
        profileService.deleteProfile(userId);
        return ResponseEntity.ok("Profile deleted successfully for userId: " + userId);
    }
}