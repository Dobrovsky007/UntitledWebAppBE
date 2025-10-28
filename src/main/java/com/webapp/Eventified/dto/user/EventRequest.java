package com.webapp.Eventified.dto.user;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * Data Transfer Object for event creation requests.
 * Contains all necessary information to create a new event.
 *
 * @author Eventified Team
 * @version 1.0
 */
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
