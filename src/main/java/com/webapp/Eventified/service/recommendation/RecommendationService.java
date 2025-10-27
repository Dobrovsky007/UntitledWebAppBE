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
import com.webapp.Eventified.domain.EventParticipant;
import com.webapp.Eventified.domain.SportUser;
import com.webapp.Eventified.domain.User;
import com.webapp.Eventified.dto.user.EventPoolDTO;
import com.webapp.Eventified.repository.EventParticipantRepository;
import com.webapp.Eventified.repository.EventRepository;
import com.webapp.Eventified.repository.UserRepository;

import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final ContentBasedScorer contentBasedScorer;
    
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

    private boolean isUserJoined(UUID userId, UUID eventId){
        return eventParticipantRepository.findByUserIdAndEventId(userId, eventId).isPresent();
    }

    private Map<UUID, Double> calculateEventScores(List<Event> events, User user, List<Event> userEventHistory){
        Map<UUID, Double> scores = new HashMap<>();

        for(Event event : events){
            double score = contentBasedScorer.calculateScore(event, user, userEventHistory);
            scores.put(event.getId(), score);
        }
        return scores;
    }
}
