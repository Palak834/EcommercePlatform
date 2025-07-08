package com.ecommerce.productservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

//Marks this class as a Spring component for dependency injection
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // Injects the JWT secret key from application properties
    @Value("${jwt.secret}")
    private String secretKey;

    // Filters incoming HTTP requests to validate JWT tokens
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
    	// Retrieves the Authorization header
    	String authHeader = request.getHeader("Authorization");
        logger.debug("Processing request for: {}", request.getRequestURI());

        // Checks if the Authorization header contains a valid Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
            	// Extracts the JWT token
            	String token = authHeader.substring(7);
            	// Parses and validates the JWT token using the secret key
            	Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey.getBytes())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                logger.info("Token validated - role: {}, subject: {}, expiration: {}", 
                        claims.get("role"), claims.getSubject(), claims.getExpiration());

                // Extracts role and subject (userId) from token claims
                String role = (String) claims.get("role");
                String userId = claims.getSubject();

                // If role and userId are present, sets up Spring Security authentication
                if (role != null && userId != null) {
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userId, null, Collections.singletonList(authority));
                    // Sets the authentication in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("Authentication set for role: {}", role);
                } else {
                	// Logs a warning if role or subject is missing
                	logger.warn("Role or subject missing in token");
                }
            } catch (JwtException e) {
            	// Handles invalid or expired JWT tokens
                logger.error("Invalid or expired JWT token: {}", e.getMessage());
                // Returns a 403 Forbidden response
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or expired token");
                return;
            }
        } else {
        	// Logs a warning if the Authorization header is missing or invalid
        	logger.warn("Authorization header missing or invalid for: {}", request.getRequestURI());
        }

        // Continues the filter chain
        chain.doFilter(request, response);
    }
}