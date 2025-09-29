package com.webapp.Eventified.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class LoginRequest {
    private String username;
    private String password;

}
