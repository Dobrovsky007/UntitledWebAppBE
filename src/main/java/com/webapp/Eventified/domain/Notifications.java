package com.webapp.Eventified.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "notifications")
public class Notifications {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "type_of_notification", nullable = false)
    private Integer typeOfNotification;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message_of_notification", nullable = false)
    private String messageOfNotification;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private UUID userId;

    @Column(name = "event_id", insertable = false, updatable = false)
    private UUID eventId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", insertable = false, nullable = false)
    private Event event;

    public Notifications() {

    }

    public Notifications(UUID userId, UUID eventId, Integer typeOfNotification, String title,
            String messageOfNotification) {
        this.userId = userId;
        this.eventId = eventId;
        this.typeOfNotification = typeOfNotification;
        this.title = title;
        this.messageOfNotification = messageOfNotification;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }
}
