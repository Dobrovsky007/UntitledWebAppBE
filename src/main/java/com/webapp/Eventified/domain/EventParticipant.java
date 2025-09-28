package com.webapp.Eventified.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import com.webapp.Eventified.domain.id.EventParticipantId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "event_participants")
@IdClass(EventParticipantId.class)
public class EventParticipant {
    
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "role_of_participant", nullable = false)
    private Integer roleOfParticipant;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @ManyToMany
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToMany
    @JoinColumn(name = "event_id", insertable = false, updatable = false)
    private Event event;

    public EventParticipant(){}

    public EventParticipant(UUID userId, UUID eventId, Integer roleOfParticipant){
        this.userId = userId;
        this.eventId = eventId;
        this.roleOfParticipant = roleOfParticipant;
        this.joinedAt = LocalDateTime.now();
    }
}
