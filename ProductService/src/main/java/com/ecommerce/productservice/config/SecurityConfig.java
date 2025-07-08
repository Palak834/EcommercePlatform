package com.ecommerce.productservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	// Injects the JwtAuthenticationFilter dependency
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
 
    // Configures the security filter chain for HTTP requests
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disables CSRF protection as the application uses JWT
                .csrf(csrf -> csrf.disable())
                // Configures stateless session management
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Defines authorization rules for HTTP requests
                .authorizeHttpRequests(auth -> auth
                		// Allows public access to GET requests for products and categories
                        .requestMatchers(HttpMethod.GET, "/product", "/product/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/category", "/category/**").permitAll()
                        // Restricts POST, PUT, DELETE requests to ADMIN role
                        .requestMatchers(HttpMethod.POST, "/product", "/category").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/product/**", "/category/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/product/**", "/category/**").hasAuthority("ADMIN")
                        // Allows public access to actuator and error endpoints
                        .requestMatchers("/actuator/**", "/error/**").permitAll()
                        .anyRequest().authenticated())
                // Adds JWT filter before CORS filter
                .addFilterBefore(jwtAuthenticationFilter, CorsFilter.class);

        // Builds and returns the security filter chain
        return http.build();
    }

    
}