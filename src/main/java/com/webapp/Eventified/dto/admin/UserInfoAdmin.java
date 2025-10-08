package com.webapp.Eventified.dto.admin;

import java.util.UUID;

import lombok.Data;

@Data
public class UserInfoAdmin {

    private UUID id;
    private String username;
    private String email;
    private Boolean isVerified;
    private Boolean isAdmin;
    private float trustScore;
}
