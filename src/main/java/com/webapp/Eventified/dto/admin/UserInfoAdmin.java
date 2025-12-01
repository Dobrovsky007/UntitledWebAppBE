package com.webapp.Eventified.dto.admin;

import java.util.UUID;

import lombok.Data;

/**
 * Data Transfer Object for user information in admin contexts.
 * Contains detailed user information including admin-specific fields.
 *
 * @author Eventified Team
 * @version 1.0
 */
@Data
public class UserInfoAdmin {

    private UUID id;
    private String username;
    private String email;
    private Boolean isVerified;
    private Boolean isAdmin;
    private float trustScore;
}
