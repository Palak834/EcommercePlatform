package com.ecommerce.registrationservice.service;

import com.ecommerce.registrationservice.entity.User;
import com.ecommerce.registrationservice.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

// Marks this class as a service component for business logic
@Service
// Enables validation for method parameters
@Validated
public class RegistrationService {
    // Logger instance for logging registration-related activities
    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    // Injects the UserRepository dependency for database operations
    @Autowired
    private UserRepository userRepository;

    // Injects the BCryptPasswordEncoder for password hashing
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Registers a new user with validated input
    public User registerUser(@Valid User user) {
        // Logs the registration attempt with the user's email
        logger.info("Registering user with email: {}", user.getEmail());
        // Checks if the email is already registered
        if (userRepository.findByEmail(user.getEmail()) != null) {
            logger.warn("Email already exists: {}", user.getEmail());
            throw new RuntimeException("Email already exists");
        }
        // Hashes the user's password for secure storage
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Sets default role to "USER" if not provided
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        // Saves the user to the database
        User savedUser = userRepository.save(user);
        // Logs successful registration with the user's role
        logger.info("User registered successfully with role: {}", savedUser.getRole());
        return savedUser;
    }
}