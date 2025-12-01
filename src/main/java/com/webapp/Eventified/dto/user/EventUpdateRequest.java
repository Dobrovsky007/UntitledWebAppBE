package com.webapp.Eventified.dto.user;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * Data Transfer Object for event update requests.
 * Contains fields that can be modified when updating an existing event.
 *
 * @author Eventified Team
 * @version 1.0
 */
@Data
public class EventUpdateRequest {

    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
