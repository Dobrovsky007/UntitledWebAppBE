package com.webapp.Eventified.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object for user login requests.
 * Contains the credentials required for user authentication.
 *
 * @author Eventified Team
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class LoginRequest {
    private String username;
    private String password;

}
