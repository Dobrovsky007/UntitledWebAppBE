package com.webapp.Eventified.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.webapp.Eventified.domain.Event;
import com.webapp.Eventified.repository.EventRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventStatusSchedulerService {

    private final EventRepository eventRepository;

    private static final Integer STATUS_PAST = 2;
    private static final Integer STATUS_ONGOING = 1;
    private static final Integer STATUS_ACTIVE = 0;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateEventStatuses() throws Exception {
        
        LocalDateTime now = LocalDateTime.now();

        int pastUpdatedCount = updateEventsToPastStatus(now);
        int ongoingUpdatedCount = updateEventsToOngoingStatus(now);

        if(pastUpdatedCount > 0 || ongoingUpdatedCount > 0){
            log.info("Event status update completed. Events marked as PAST: {}, Events marked as ONGOING: {}", pastUpdatedCount, ongoingUpdatedCount);
        }
    }

    private int updateEventsToPastStatus(LocalDateTime now){

            List<Event> eventsToUpdate = eventRepository.findEventsToMarkAsPast(now, STATUS_PAST);

            if(!eventsToUpdate.isEmpty()){
                log.info("Found {} events to update to PAST status", eventsToUpdate.size());

                for(Event event : eventsToUpdate){
                    if (event.getStatusOfEvent() != 3) {
                        event.setStatusOfEvent(STATUS_PAST);
                    }
                }
                
                eventRepository.saveAll(eventsToUpdate);
                return eventsToUpdate.size();
            }
            return 0;
        }
    
    private int updateEventsToOngoingStatus(LocalDateTime now){
        List<Event> eventsToUpdate = eventRepository.findEventsToMarkAsOngoing(now, STATUS_ACTIVE, STATUS_ONGOING);

        if(!eventsToUpdate.isEmpty()){
            log.info("Found {} events to update to ONGOING status", eventsToUpdate.size());

            for(Event event : eventsToUpdate){
                if (event.getStatusOfEvent() != 3) {
                    event.setStatusOfEvent(STATUS_ONGOING);
                }
            }

            eventRepository.saveAll(eventsToUpdate);
            return eventsToUpdate.size();
        }
        return 0;
    }
}
