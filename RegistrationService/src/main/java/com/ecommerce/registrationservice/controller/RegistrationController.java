package com.ecommerce.registrationservice.controller;

import com.ecommerce.registrationservice.entity.User;
import com.ecommerce.registrationservice.service.RegistrationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
public class RegistrationController {
	// Logger for debugging and logging registration activities
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private RegistrationService registrationService;

    // Handles POST requests to register a new user
    @PostMapping
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
    	// Logs the registration attempt with the user's email
    	logger.info("Received registration request for email: {}", user.getEmail());
    	// Calls the service to register the user and returns the saved user
    	return ResponseEntity.ok(registrationService.registerUser(user));
    }
}