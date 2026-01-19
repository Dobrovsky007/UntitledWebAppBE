package com.webapp.Eventified.dto.user;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import lombok.Data;

@Data
public class EventDetailsDTO {

    private UUID id;
    private String title;
    private Integer sport;
    private Integer skillLevel;
    private String address;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer capacity;
    private Integer occupied;
    private Set<EventParticipantDTO> participants;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private OrganizerDTO organizer;
    private Integer statusOfEvent;
    private Boolean rated;
}

class OrganizerDTO {
    public String username;
    public UUID id;
    
    public OrganizerDTO(String username, UUID id) {
        this.username = username;
        this.id = id;
    }
}
