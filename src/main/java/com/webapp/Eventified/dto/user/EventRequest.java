package com.webapp.Eventified.dto.user;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EventRequest {
    private String title;
    private Integer sport;
    private String address;
    private Integer skillLevel;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer capacity;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
