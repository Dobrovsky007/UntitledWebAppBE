package com.webapp.Eventified.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for the Eventified application.
 * Provides API documentation with JWT authentication support.
 * 
 * @author Eventified Team
 * @version 1.0
 */
@Configuration
public class OpenApiConfig {

    @Value("${app.security.enabled:true}")
    private boolean securityEnabled;

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Configures OpenAPI documentation with JWT security.
     * 
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Eventified Backend API")
                        .description("REST API for the Eventified event management platform")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Eventified Team")
                                .email("team@eventified.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Local server")
                ));

        // Add JWT authentication only if security is enabled
        if (securityEnabled) {
            openAPI.addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                    .components(new Components()
                            .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
        }

        return openAPI;
    }

    /**
     * Creates the JWT security scheme for Swagger UI.
     * 
     * @return SecurityScheme for JWT
     */
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer")
                .description("Enter JWT token (without 'Bearer ' prefix)");
    }
}