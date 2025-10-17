package com.webapp.Eventified.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("SELECT e FROM Event e WHERE e.endTime < :currentTime AND e.statusOfEvent != :pastStatus")
    List<Event> findEventsToMarkAsPast(@Param("currentTime") LocalDateTime currentTime, @Param("pastStatus") Integer pastStatus);

    @Query("SELECT e FROM Event e WHERE e.startTime <= :currentTime AND e.endTime > :currentTime AND e.statusOfEvent = :activeStatus")
    List<Event> findEventsToMarkAsOngoing(@Param("currentTime") LocalDateTime currentTime, @Param("activeStatus") Integer activeStatus, @Param("ongoingStatus") Integer ongoingStatus);
   

    List<Event> findEventByStatusAndStartTime(int i, LocalDateTime reminderTime);
    List<Event> findEventByStatusAndEndTime(Integer statusPast, LocalDateTime ratingReminderTime);
}
