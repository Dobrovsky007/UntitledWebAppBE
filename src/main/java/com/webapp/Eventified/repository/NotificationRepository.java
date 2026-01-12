package com.webapp.Eventified.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webapp.Eventified.model.Notification;

/**
 * Repository interface for Notification entity operations.
 * Provides database access methods for managing user notifications.
 *
 * @author Eventified Team
 * @version 1.0
 */
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    /**
     * Finds all notifications for a specific user, ordered by creation time (newest first).
     *
     * @param id the unique identifier of the user
     * @return List of notifications ordered by creation date descending
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID id);

    /**
     * Finds notifications for a specific user filtered by read status, ordered by creation time.
     *
     * @param id the unique identifier of the user
     * @param b the read status to filter by (true for read, false for unread)
     * @return List of notifications matching the criteria ordered by creation date descending
     */
    List<Notification> findByUserIdAndIsReadOrderByCreatedAtDesc(UUID id, boolean b);

    /**
     * Counts the number of notifications for a user with a specific read status.
     *
     * @param id the unique identifier of the user
     * @param b the read status to count (true for read, false for unread)
     * @return the count of notifications matching the criteria
     */
    Long countByUserIdAndIsRead(UUID id, boolean b);

    /**
     * Finds all notifications for a specific user.
     *
     * @param id the unique identifier of the user
     * @return List of all notifications for the user
     */
    List<Notification> findByUserId(UUID id);

    boolean existsByUser_IdAndEvent_IdAndTypeOfNotification(UUID userId, UUID eventId, Integer typeOfNotification);
    
}
