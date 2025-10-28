package com.webapp.Eventified.service.recommendation;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webapp.Eventified.domain.Event;
import com.webapp.Eventified.domain.SportUser;
import com.webapp.Eventified.domain.User;
import com.webapp.Eventified.dto.user.EventPoolDTO;
import com.webapp.Eventified.repository.EventParticipantRepository;
import com.webapp.Eventified.repository.EventRepository;
import com.webapp.Eventified.repository.UserRepository;

import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for providing personalized event recommendations to users.
 * Uses content-based filtering to score and rank events based on user preferences and history.
 *
 * @author Eventified Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final ContentBasedScorer contentBasedScorer;
    
    /**
     * Retrieves personalized event recommendations for a user.
     * Generates recommendations based on user's sport preferences, past event history, and event scores.
     * Returns events sorted by recommendation score in descending order.
     *
     * @param username the username of the user requesting recommendations
     * @param limit the maximum number of recommendations to return
     * @return List of recommended events as EventPoolDTO objects
     * @throws IllegalArgumentException if the user is not found
     */
    @Transactional(readOnly = true)
    public List<EventPoolDTO> getRecommendedEvents(String username, int limit){
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new  IllegalArgumentException("User not found"));

        List<Event> userEventHistory = eventParticipantRepository.findAllEventsByUserId(user.getId());

        List<Event> candidateEvents = getCandidateEvents(user);

        if(candidateEvents.isEmpty()){
            log.info("No candidate events found for user: {}", username);
            return Collections.emptyList();
        }

        Map<UUID, Double> eventScores = calculateEventScores(candidateEvents, user, userEventHistory);

        List<EventPoolDTO> recommendations = candidateEvents.stream()
            .sorted((e1,e2) -> Double.compare(
                eventScores.getOrDefault(e2.getId(), 0.0),
                eventScores.getOrDefault(e1.getId(), 0.0)
            ))
            .limit(limit)
            .map(EventPoolDTO::new)
            .collect(Collectors.toList());

        return recommendations;
    }

    /**
     * Retrieves candidate events for recommendation based on user's sports preferences and history.
     * Filters events to include only upcoming events the user hasn't joined.
     * Includes events from both user's preferred sports and sports from their event history.
     *
     * @param user the user for whom to retrieve candidate events
     * @return List of candidate events for recommendation
     */
    private List<Event> getCandidateEvents(User user){

        LocalDateTime now = LocalDateTime.now();
        Set<SportUser> userSports = user.getSports();

        if(userSports != null && !userSports.isEmpty()){
            Set<Integer> sportIds = userSports.stream()
            .map(SportUser::getSport)
            .collect(Collectors.toSet());

            List<Event> prefferedSportEvents = eventRepository.findEventsBySports(sportIds, now);

            Set<Integer> historicalSports = eventParticipantRepository.findSportsFromUserHistory(user.getId());

            if(!historicalSports.isEmpty() && !historicalSports.equals(sportIds)){
                List<Event> historicalSportEvents = eventRepository.findEventsBySports(historicalSports, now);
                prefferedSportEvents.addAll(historicalSportEvents);
            }

            return prefferedSportEvents.stream()
                .distinct()
                .filter(event -> !isUserJoined(user.getId(), event.getId()))
                .collect(Collectors.toList());    
        }
        return eventRepository.findUpcomingEventsNotAttendedByUser(user.getId(), now);  
    }

    /**
     * Checks whether a user has already joined a specific event.
     *
     * @param userId the unique identifier of the user
     * @param eventId the unique identifier of the event
     * @return true if the user has joined the event, false otherwise
     */
    private boolean isUserJoined(UUID userId, UUID eventId){
        return eventParticipantRepository.findByUserIdAndEventId(userId, eventId).isPresent();
    }

    /**
     * Calculates recommendation scores for all candidate events.
     * Uses the ContentBasedScorer to compute a score for each event based on user preferences and history.
     *
     * @param events the list of candidate events to score
     * @param user the user for whom to calculate scores
     * @param userEventHistory the list of events the user has previously attended
     * @return Map of event IDs to their calculated recommendation scores
     */
    private Map<UUID, Double> calculateEventScores(List<Event> events, User user, List<Event> userEventHistory){
        Map<UUID, Double> scores = new HashMap<>();

        for(Event event : events){
            double score = contentBasedScorer.calculateScore(event, user, userEventHistory);
            scores.put(event.getId(), score);
        }
        return scores;
    }
}
