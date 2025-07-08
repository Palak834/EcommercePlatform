package com.ecommerce.loginservice.controller;

import com.ecommerce.loginservice.dto.LoginRequest;
import com.ecommerce.loginservice.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;

    // Handles POST requests for user login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody LoginRequest request) {
        logger.info("Received login request for email: {}", request.getEmail());
        // Authenticates the user and generates a JWT token
        String token = loginService.loginUser(request.getEmail(), request.getPassword());
        // Prepares the response with the JWT token
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        // Returns 200 OK with the token
        return ResponseEntity.ok(response);
    }
}