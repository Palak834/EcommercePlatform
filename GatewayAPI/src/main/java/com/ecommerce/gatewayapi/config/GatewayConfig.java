package com.ecommerce.gatewayapi.config;

import com.ecommerce.gatewayapi.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class GatewayConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("RegistrationService", r -> r.path("/register/**")
                        .uri("lb://REGISTRATIONSERVICE"))
                .route("LoginService", r -> r.path("/auth/**")
                        .uri("lb://LOGINSERVICE"))
                .route("ProductService", r -> r.path("/product/**")
                        .uri("lb://PRODUCTSERVICE"))
                .route("ProfileService", r -> r.path("/profile/**")
                        .uri("lb://PROFILESERVICE"))
                .route("CartService", r -> r.path("/cart/**")
                        .uri("lb://CARTSERVICE"))
                .route("OrderService", r -> r.path("/order/**")
                        .uri("lb://ORDERSERVICE"))
                .route("PaymentService", r -> r.path("/payment/**")
                        .uri("lb://PAYMENTSERVICE"))
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}