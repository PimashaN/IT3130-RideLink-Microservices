package com.ridelink.driver_service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Accessible Swagger endpoints
                .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/swagger-config"
                ).permitAll()
                
                // Update the Driver location/availability
                .requestMatchers(HttpMethod.PATCH, "/api/drivers/{id}/**").hasAnyAuthority("ROLE_DRIVER", "ROLE_ADMIN")
                
                // Create or delete vehicles and drivers - only accessible by Admin or the respective Driver
                .requestMatchers(HttpMethod.POST, "/api/drivers/**", "/api/vehicles/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DRIVER")
                .requestMatchers(HttpMethod.DELETE, "/api/drivers/**", "/api/vehicles/**").hasAuthority("ROLE_ADMIN")
                
                // Search for drivers (for use by Ride Service)
                .requestMatchers("/api/drivers/search", "/api/drivers/nearby", "/api/drivers/available").hasAnyAuthority("ROLE_ADMIN", "ROLE_DRIVER", "ROLE_RIDE")
                
                // All other requests require Authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}