package com.webapp.Eventified.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for password encoding.
 * Separated from SecurityConfig to avoid circular dependency issues.
 * 
 * @author Eventified Team
 * @version 1.0
 */
@Configuration
public class PasswordConfig {

    /**
     * Provides the password encoder bean.
     * 
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}