package com.webapp.Eventified.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object for login response.
 * Contains the JWT authentication token returned after successful login.
 *
 * @author Eventified Team
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
}
