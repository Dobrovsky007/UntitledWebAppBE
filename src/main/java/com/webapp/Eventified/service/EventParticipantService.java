package com.webapp.Eventified.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.webapp.Eventified.model.EventParticipant;
import com.webapp.Eventified.model.User;
import com.webapp.Eventified.repository.EventParticipantRepository;
import com.webapp.Eventified.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventParticipantService {

    private final EventParticipantRepository eventParticipantRepository;
    private final UserRepository userRepository;

    public List<User> getAllParticipantsOfEvent(UUID eventId){
        return eventParticipantRepository.findByEventId(eventId)
                .stream()
                .map(EventParticipant::getUser)
                .collect(Collectors.toList());
    }

    public boolean giveTrustScoreToParticipant(UUID participantId, int trustScore, String organizerUsername, UUID eventId) {

        User organizer = userRepository.findByUsername(organizerUsername)
                .orElseThrow(() -> new IllegalArgumentException("Organizer not found with username: " + organizerUsername));
    
        EventParticipant eventParticipation = eventParticipantRepository.findByEventIdAndUserId(eventId, organizer.getId())
                .orElseThrow(() -> new IllegalArgumentException("Organizer is not a participant of the event with id: " + eventId));

        User participant = userRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + participantId));

        participant.setTrustScore(trustScore);
        userRepository.save(participant);
        return true;
    }
    
}
