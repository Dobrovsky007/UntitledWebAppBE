package com.webapp.Eventified.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webapp.Eventified.domain.Event;

public interface EventRepository extends JpaRepository<Event, UUID> { 
    Optional<Event> findByTitle(String title);
    Optional<Event> findByCreatorId(UUID userId);
    Optional<Event> findByTitleAndOrganizer_Id(String title, UUID organizerId);
}
