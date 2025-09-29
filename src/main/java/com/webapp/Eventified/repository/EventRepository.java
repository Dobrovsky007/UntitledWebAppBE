package com.webapp.Eventified.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webapp.Eventified.domain.Event;
import com.webapp.Eventified.domain.User;

public interface EventRepository extends JpaRepository<Event, UUID> { 
    Optional<Event> findByTitle(String title);
    Optional<Event> findByOrganizer_Id(UUID userId);
    Optional<Event> findByTitleAndOrganizer(String title, User organizer);
}
