package com.ecommerce.loginservice.service;

import com.ecommerce.loginservice.entity.User;
import com.ecommerce.loginservice.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    // Injects the UserRepository dependency
    @Autowired
    private UserRepository userRepository;

    // Injects the BCryptPasswordEncoder dependency
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Injects the JWT secret key from application properties
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Authenticates a user and generates a JWT token
    public String loginUser(String email, String password) {
        logger.info("Login attempt for email: {}", email);
        // Finds the user by email
        User user = userRepository.findByEmail(email);
        if (user == null) {
        	// Logs and throws an exception if user is not found
            logger.warn("User not found with email: {}", email);
            throw new RuntimeException("User not found");
        }
        
        // Verifies the password
        if (!passwordEncoder.matches(password, user.getPassword())) {
        	// Logs and throws an exception if password is invalid
        	logger.warn("Invalid password for email: {}", email);
            throw new RuntimeException("Invalid password");
        }
        // Generates a JWT token for the authenticated user
        String token = generateToken(user);
        // Logs successful login
        logger.info("Login successful for email: {}, generated token with role: {}", email, user.getRole());
        return token;
    }

    // Generates a JWT token for the user
    private String generateToken(User user) {
    	// Prepares claims for the JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        // Sets token issuance and expiration times
        long now = System.currentTimeMillis();
        long expirationTime = now + 1000 * 60 * 60 * 24; // 24 hours expiration
        
        // Builds and signs the JWT
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expirationTime))
                .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes())
                .compact();
    }
}