package com.ecommerce.loginservice.controller;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.WeakKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

//Marks this class as a global exception handler for REST controllers
@RestControllerAdvice
public class GlobalExceptionHandler {
	// Logger for debugging and logging exception handling
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handles AccessDeniedException for unauthorized access attempts
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
        logger.error("Access denied error: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Access denied: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    // Handles WeakKeyException for JWT key issues
    @ExceptionHandler(WeakKeyException.class)
    public ResponseEntity<Map<String, String>> handleWeakKeyException(WeakKeyException ex) {
        logger.error("JWT key error: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid JWT key configuration: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handles JwtException for JWT processing issues
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, String>> handleJwtException(JwtException ex) {
        logger.error("JWT processing error: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "JWT error: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Handles RuntimeException for general runtime errors
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        logger.error("Runtime error: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Handles generic Exception for unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal server error");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}