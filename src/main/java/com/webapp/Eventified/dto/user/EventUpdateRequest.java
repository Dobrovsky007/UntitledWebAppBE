package com.webapp.Eventified.dto.user;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EventUpdateRequest {

    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
