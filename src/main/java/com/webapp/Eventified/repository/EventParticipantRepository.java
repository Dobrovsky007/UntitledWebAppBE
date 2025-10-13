package com.webapp.Eventified.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webapp.Eventified.domain.EventParticipant;
import com.webapp.Eventified.domain.id.EventParticipantId;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, EventParticipantId> {
    Optional<EventParticipant> findByUserIdAndEventId(UUID userId, UUID eventId);
    Optional<EventParticipant> findByUserIdAndRoleOfParticipant(UUID userId, Integer roleOfParticipant);
}
