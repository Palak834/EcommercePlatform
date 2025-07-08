package com.ecommerce.profileservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

// Security configuration for ProfileService
@Configuration
// Enables Spring Security
@EnableWebSecurity
public class SecurityConfig {

    // Inject JWT authentication filter
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Configures security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Allow GET, PUT, DELETE on /profile/** for USER and ADMIN
                        .requestMatchers(HttpMethod.GET, "/profile/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/profile/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/profile/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers("/actuator/**", "/error/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, CorsFilter.class);

        return http.build();
    }

}