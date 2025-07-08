package com.ecommerce.registrationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	// Configures and provides a BCryptPasswordEncoder bean for password hashing
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
    	// Returns a new instance of BCryptPasswordEncoder for secure password encoding
        return new BCryptPasswordEncoder();
    }

    // Configures the security filter chain for HTTP requests
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configures authorization rules for HTTP requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register/**", "/actuator/**").permitAll()
                        // Requires authentication for all other requests
                        .anyRequest().authenticated())
                // Disables CSRF protection as this is a stateless API
                .csrf(csrf -> csrf.disable());
        // Builds and returns the configured security filter chain
        return http.build();
    }
}