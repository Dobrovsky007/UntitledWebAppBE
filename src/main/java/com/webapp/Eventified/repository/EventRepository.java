package com.webapp.Eventified.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.webapp.Eventified.domain.Event;
import com.webapp.Eventified.domain.User;

/**
 * Repository interface for Event entity operations.
 * Provides database access methods for event management and queries.
 *
 * @author Eventified Team
 * @version 1.0
 */
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
   

    List<Event> findEventByStatusOfEventAndStartTime(int i, LocalDateTime reminderTime);
    List<Event> findEventByStatusOfEventAndEndTime(Integer statusPast, LocalDateTime ratingReminderTime);

     @Query("SELECT e FROM Event e WHERE e.startTime > :now " +
           "AND e.statusOfEvent = 0 " +
           "AND e.id NOT IN (SELECT ep.eventId FROM EventParticipant ep WHERE ep.userId = :userId)")
    List<Event> findUpcomingEventsNotAttendedByUser(@Param("userId") UUID userId, @Param("now") LocalDateTime now);
    
    @Query("SELECT e FROM Event e WHERE e.sport IN :sportIds " +
           "AND e.startTime > :now AND e.statusOfEvent = 0")
    List<Event> findEventsBySports(@Param("sportIds") Set<Integer> sportIds, @Param("now") LocalDateTime now);
    
    @Query("SELECT e FROM Event e WHERE e.skillLevel BETWEEN :minSkill AND :maxSkill " +
           "AND e.startTime > :now AND e.statusOfEvent = 0")
    List<Event> findEventsBySkillRange(@Param("minSkill") Integer minSkill, @Param("maxSkill") Integer maxSkill, @Param("now") LocalDateTime now);
}
