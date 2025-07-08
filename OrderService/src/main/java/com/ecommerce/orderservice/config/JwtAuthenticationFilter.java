package com.ecommerce.orderservice.config;

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

// Component for JWT authentication filter
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // Injecting the JWT secret key from properties
    @Value("${jwt.secret}")
    private String secretKey;

    // Method to filter incoming requests
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        logger.debug("Processing request for: {}", request.getRequestURI());

        // Check if the Authorization header is present and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
            	// Extract the token
                String token = authHeader.substring(7);
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey.getBytes())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                // Log token details
                logger.info("Token validated - role: {}, subject: {}, expiration: {}", 
                        claims.get("role"), claims.getSubject(), claims.getExpiration());

                // Extract role and user ID from claims
                String role = (String) claims.get("role");
                String userId = claims.getSubject();

                // If role and user ID are present, set authentication context
                if (role != null && userId != null) {
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userId, null, Collections.singletonList(authority));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    // Log successful authentication
                    logger.info("Authentication set for role: {}", role);
                } else {
                	// Log warning if role or subject is missing in token
                    logger.warn("Role or subject missing in token");
                }
            } catch (JwtException e) {
            	// Log JWT validation errors
                logger.error("Invalid or expired JWT token: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or expired token");
                return;
            }
        } else {
        	// Log warning if Authorization header is missing or invalid
            logger.warn("Authorization header missing or invalid for: {}", request.getRequestURI());
        }

        chain.doFilter(request, response);
    }
}