package com.ecommerce.profileservice.config;

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

// JWT filter for authenticating requests
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	// Logger for tracking filter operations
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // JWT secret key from configuration
    @Value("${jwt.secret}")
    private String secretKey;

    // Filters incoming requests to validate JWT
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        logger.debug("Processing request for: {}", request.getRequestURI());

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey.getBytes())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                logger.info("Token validated - role: {}, subject: {}, expiration: {}", 
                        claims.get("role"), claims.getSubject(), claims.getExpiration());

                // Extract role and subject from claims
                String role = (String) claims.get("role");
                String userId = claims.getSubject();

                // Set authentication if role and userId are present
                if (role != null && userId != null) {
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userId, null, Collections.singletonList(authority));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("Authentication set for role: {}", role);
                } else {
                    logger.warn("Role or subject missing in token");
                }
            } catch (JwtException e) {
            	// Handle invalid or expired JWT
                logger.error("Invalid or expired JWT token: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or expired token");
                return;
            }
        } else {
        	// Log missing or invalid Authorization header
            logger.warn("Authorization header missing or invalid for: {}", request.getRequestURI());
        }

        chain.doFilter(request, response);
    }
}