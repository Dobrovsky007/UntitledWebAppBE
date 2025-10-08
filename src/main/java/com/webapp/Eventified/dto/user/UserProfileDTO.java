package com.webapp.Eventified.dto.user;

import java.util.List;

import lombok.Data;

@Data
public class UserProfileDTO {

    private String username;
    private float rating;
    private List<SportDTO> sports;
    private boolean isVerified;
}
