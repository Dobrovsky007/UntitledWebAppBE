package com.webapp.Eventified.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserProfileDTO {

    private String username;
    private boolean isAdmin;
    private float rating;
    private List<SportDTO> sports;
}
