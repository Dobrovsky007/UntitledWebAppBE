package com.webapp.Eventified.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webapp.Eventified.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID id);

    List<Notification> findByUserIdAndIsReadOrderByCreatedAtDesc(UUID id, boolean b);

    Long countByUserIdAndIsRead(UUID id, boolean b);

    List<Notification> findByUserId(UUID id);
    
}
