package com.webapp.Eventified.dto.user;

import java.util.List;

import lombok.Data;

/**
 * Data Transfer Object for user profile information.
 * Contains user details displayed in profile views.
 *
 * @author Eventified Team
 * @version 1.0
 */
@Data
public class UserProfileDTO {

    private String username;
    private float rating;
    private List<SportDTO> sports;
    private boolean isVerified;
}
