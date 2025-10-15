package com.webapp.Eventified.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webapp.Eventified.domain.Notifications;

public interface NotificationRepository extends JpaRepository<Notifications, UUID> {
    
}
