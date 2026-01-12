package com.webapp.Eventified.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.webapp.Eventified.model.Event;
import com.webapp.Eventified.model.EventParticipant;
import com.webapp.Eventified.model.User;
import com.webapp.Eventified.repository.EventParticipantRepository;
import com.webapp.Eventified.repository.EventRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RatingService {

    // Keep consistent with existing scheduler/status usage
    private static final Integer STATUS_PAST = 2;

    // Minimal default scale; adjust if your UI uses different bounds
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;

    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;

    /**
     * Organizer submits ratings for all participants of an event.
     *
     * Uses existing entities only:
     * - Prevents re-rating per event via Event.rated flag
     * - Updates User.trustScore as SUM of ratings and increments numberOfReviews
     */
    @Transactional
    public void submitEventParticipantRatings(String organizerUsername, UUID eventId, Map<String, Integer> ratingsByParticipantUsername) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        User organizer = event.getOrganizer();
        if (organizer == null || organizer.getUsername() == null || !organizer.getUsername().equals(organizerUsername)) {
            throw new IllegalArgumentException("Only the event organizer can rate participants");
        }

        LocalDateTime now = LocalDateTime.now();
        if (event.getEndTime() == null || event.getEndTime().isAfter(now)) {
            throw new IllegalArgumentException("Event has not ended yet");
        }

        if (event.getStatusOfEvent() == null || !event.getStatusOfEvent().equals(STATUS_PAST)) {
            throw new IllegalArgumentException("Event is not in PAST status");
        }

        if (Boolean.TRUE.equals(event.getRated())) {
            throw new IllegalArgumentException("Event participants were already rated");
        }

        List<EventParticipant> participants = eventParticipantRepository.findByEventId(eventId);

        UUID organizerId = organizer.getId();
        Set<String> rateableParticipantUsernames = participants.stream()
            .map(EventParticipant::getUser)
            .filter(user -> user != null)
            .filter(user -> organizerId == null || user.getId() == null || !organizerId.equals(user.getId()))
            .map(User::getUsername)
            .collect(Collectors.toSet());

        if (rateableParticipantUsernames.isEmpty()) {
            event.setRated(true);
            eventRepository.save(event);
            return;
        }

        if (ratingsByParticipantUsername == null || ratingsByParticipantUsername.isEmpty()) {
            throw new IllegalArgumentException("No ratings submitted");
        }

        // Minimal safeguard: require a rating for each participant (since we can't track partial completion without extra schema)
        if (!ratingsByParticipantUsername.keySet().equals(rateableParticipantUsernames)) {
            throw new IllegalArgumentException("Ratings must be provided for all event participants");
        }

        List<User> usersToUpdate = participants.stream()
                .map(EventParticipant::getUser)
                .filter(user -> user != null && user.getUsername() != null)
                .filter(user -> rateableParticipantUsernames.contains(user.getUsername()))
                .collect(Collectors.toList());

        for (User participant : usersToUpdate) {
            Integer rating = ratingsByParticipantUsername.get(participant.getUsername());
            if (rating == null || rating < MIN_RATING || rating > MAX_RATING) {
                throw new IllegalArgumentException("Invalid rating for user " + participant.getUsername());
            }

            participant.setTrustScore(participant.getTrustScore() + rating);
            participant.setNumberOfReviews(participant.getNumberOfReviews() + 1);
        }

        // Mark event as fully rated (completed)
        event.setRated(true);
        eventRepository.save(event);
    }
}
