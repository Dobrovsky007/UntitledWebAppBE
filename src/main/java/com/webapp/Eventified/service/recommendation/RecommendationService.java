package com.webapp.Eventified.service.recommendation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.webapp.Eventified.domain.Event;
import com.webapp.Eventified.domain.EventParticipant;
import com.webapp.Eventified.domain.SportUser;
import com.webapp.Eventified.domain.User;
import com.webapp.Eventified.dto.user.EventPoolDTO;
import com.webapp.Eventified.repository.EventParticipantRepository;
import com.webapp.Eventified.repository.EventRepository;
import com.webapp.Eventified.repository.UserRepository;

import jakarta.transaction.Transactional;
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

        List<Event> candidateEvents = get
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

            

        }



        
    }
}
