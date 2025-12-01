package com.webapp.Eventified.dto.user;


import java.util.UUID;
import com.webapp.Eventified.model.Event;

import lombok.Data;

/**
 * Data Transfer Object for event pool/listing information.
 * Contains a subset of event information suitable for displaying in event lists.
 *
 * @author Eventified Team
 * @version 1.0
 */
@Data
public class EventPoolDTO {
    private UUID id;
    private String title;
    private Integer sport;
    private String address;
    private String startTime;
    private Integer capacity;
    private Integer skillLevel;

    /**
     * Constructs an EventPoolDTO from an Event entity.
     * Converts the event's data into a format suitable for API responses.
     *
     * @param event the Event entity to convert
     */
    public EventPoolDTO(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.sport = event.getSport();
        this.address = event.getAddress();
        this.startTime = event.getStartTime().toString();
        this.capacity = event.getCapacity();
        this.skillLevel = event.getSkillLevel();
    }
}
