package com.webapp.Eventified.controller.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Development-only controller for testing backend functionality.
 * This controller is only active when security is disabled (dev mode).
 * 
 * @author Eventified Team
 * @version 1.0
 */
@RestController
@RequestMapping("/dev")
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "false")
public class DevController {

    @Value("${spring.profiles.active:unknown}")
    private String activeProfile;

    /**
     * Health check endpoint for development.
     * 
     * @return status information
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "OK");
        status.put("message", "Backend is running in development mode");
        status.put("profile", activeProfile);
        status.put("security", "DISABLED");
        status.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(status);
    }

    /**
     * Information about available endpoints in development mode.
     * 
     * @return endpoint information
     */
    @GetMapping("/endpoints")
    public ResponseEntity<Map<String, Object>> getEndpoints() {
        Map<String, Object> info = new HashMap<>();
        info.put("message", "All endpoints are accessible in development mode");
        info.put("swagger_ui", "/swagger-ui.html");
        info.put("api_docs", "/v3/api-docs");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("Auth", "/api/auth/*");
        endpoints.put("Users", "/api/user/*");
        endpoints.put("Events", "/api/events/*");
        endpoints.put("Admin", "/api/admin/*");
        endpoints.put("Development", "/api/dev/*");
        
        info.put("available_endpoints", endpoints);
        
        return ResponseEntity.ok(info);
    }
}