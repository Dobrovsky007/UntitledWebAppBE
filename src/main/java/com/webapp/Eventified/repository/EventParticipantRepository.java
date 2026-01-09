package com.webapp.Eventified.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.webapp.Eventified.model.Event;
import com.webapp.Eventified.model.EventParticipant;
import com.webapp.Eventified.model.User;
import com.webapp.Eventified.model.id.EventParticipantId;

/**
 * Repository interface for EventParticipant entity operations.
 * Provides database access methods for managing event participation and retrieving participant history.
 *
 * @author Eventified Team
 * @version 1.0
 */
public interface EventParticipantRepository extends JpaRepository<EventParticipant, EventParticipantId> {
    Optional<EventParticipant> findByUserIdAndEventId(UUID userId, UUID eventId);
    Optional<EventParticipant> findByUserIdAndRoleOfParticipant(UUID userId, Integer roleOfParticipant);
    List<EventParticipant> findByEventId(UUID eventId);

    @Query("SELECT e FROM EventParticipant ep " + 
            "JOIN Event e ON ep.eventId = e.id WHERE ep.userId = :userId " + 
            "ORDER BY e.startTime DESC")
    List<Event> findAllEventsByUserId(@Param("userId")UUID userId);


    @Query("SELECT DISTINCT e.sport FROM EventParticipant ep " +
           "JOIN Event e ON ep.eventId = e.id " +
           "WHERE ep.userId = :userId")
    Set<Integer> findSportsFromUserHistory(@Param("userId")UUID userId);

    @Query("SELECT e FROM EventParticipant ep JOIN Event e ON ep.eventId = e.id " +
           "WHERE ep.userId = :userId " +
           "AND e.endTime < :now " +
           "ORDER BY e.startTime DESC")
    List<Event> findPastEventsByUserId(@Param("userID")UUID userId, @Param("now") LocalDateTime now);
       Optional<EventParticipant> findByEventIdAndUserId(UUID eventId, UUID userId);
}
