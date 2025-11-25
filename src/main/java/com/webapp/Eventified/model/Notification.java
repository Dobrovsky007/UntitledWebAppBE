package com.webapp.Eventified.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing a notification sent to users about events.
 * Notifications can be for various purposes like event reminders, cancellations, or updates.
 *
 * @author Eventified Team
 * @version 1.0
 */
@Getter
@Setter
@ToString(exclude = {"user", "event"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "notifications")
public class Notification {

    @EqualsAndHashCode.Include
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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = true)
    private Event event;

    /**
     * Default constructor for JPA.
     */
    public Notification() {

    }

    /**
     * Constructs a new Notification with the specified details.
     * Sets the notification as unread and records the creation timestamp.
     *
     * @param userId the unique identifier of the user receiving the notification
     * @param eventId the unique identifier of the event related to the notification
     * @param typeOfNotification the integer identifier of the notification type
     * @param title the title/subject of the notification
     * @param messageOfNotification the detailed message content of the notification
     */
    public Notification(User user, Event event, Integer typeOfNotification, String title,
            String messageOfNotification) {
        this.user = user;
        this.event = event;
        this.typeOfNotification = typeOfNotification;
        this.title = title;
        this.messageOfNotification = messageOfNotification;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }
}
