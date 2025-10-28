package com.webapp.Eventified.dto.user;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for user registration requests.
 * Contains all necessary information to create a new user account.
 *
 * @author Eventified Team
 * @version 1.0
 */
@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
}
