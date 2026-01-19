package com.webapp.Eventified.dto.user;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrganizerDTO {
    private String username;
    private UUID id;
}
