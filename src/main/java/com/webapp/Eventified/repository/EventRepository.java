package com.webapp.Eventified.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webapp.Eventified.domain.Event;
import com.webapp.Eventified.domain.User;

public interface EventRepository extends JpaRepository<Event, UUID> { 
    Optional<Event> findByTitle(String title);
    Optional<Event> findByOrganizer_Id(UUID userId);
    Optional<Event> findByTitleAndOrganizer(String title, User organizer);
    List<Event> findByOrganizer(User user);
    List<Event> findBySport(Integer sport);
    List<Event> findBySkillLevel(Integer skillLevel);
    List<Event> findByStartTimeAfter(LocalDateTime dateTime);
    List<Event> findByEndTimeBefore(LocalDateTime dateTime);
}
