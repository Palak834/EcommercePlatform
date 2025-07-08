package com.ecommerce.gatewayapi.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtFilter implements GatewayFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        String normalizedPath = path.toLowerCase();

        // Skip JWT validation for public endpoints
        if (normalizedPath.matches("/register/.*") || 
            normalizedPath.matches("/auth/.*") || 
            normalizedPath.matches("/product/.*") ||
            normalizedPath.matches("/product")) {
            logger.info("Skipping JWT validation for path: {}", path);
            return chain.filter(exchange);
        }

        logger.info("Applying JWT validation for path: {}", path);
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Missing or invalid Authorization header for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            String token = authHeader.substring(7);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Log token details for debugging
            logger.info("Token validated - role: {}, subject: {}, expiration: {}", 
                    claims.get("role"), claims.getSubject(), claims.getExpiration());

            // Extract role and add as a header
            String role = (String) claims.get("role");
            exchange.getRequest().mutate()
                    .header("X-Role", role != null ? role : "UNKNOWN")
                    .header("userId", claims.getSubject())
                    .build();
        } catch (JwtException e) {
            logger.error("Invalid or expired JWT token for path: {}", path, e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }
}