package com.webapp.Eventified.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.webapp.Eventified.model.id.EventParticipantId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing the participation relationship between a user and an event.
 * Uses a composite key of userId and eventId.
 *
 * @author Eventified Team
 * @version 1.0
 */
@Getter
@Setter
@ToString(exclude = {"user", "event"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "event_participants")
@IdClass(EventParticipantId.class)
public class EventParticipant {
    
    @EqualsAndHashCode.Include
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "role_of_participant", nullable = false)
    private Integer roleOfParticipant;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", insertable = false, updatable = false)
    private Event event;

    /**
     * Default constructor for JPA.
     */
    public EventParticipant(){}

    /**
     * Constructs a new EventParticipant with the specified user and event IDs.
     * Sets the default role to 1 (regular participant) and records the join timestamp.
     *
     * @param userId the unique identifier of the user joining the event
     * @param eventId the unique identifier of the event being joined
     */
    public EventParticipant(UUID userId, UUID eventId){
        this.userId = userId;
        this.eventId = eventId;
        this.roleOfParticipant = 1;
        this.joinedAt = LocalDateTime.now();
    }
}
