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

/**
 * Scheduled service for managing event status updates and notifications.
 * Runs periodic tasks to update event statuses and send reminders.
 *
 * @author Eventified Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventStatusSchedulerService {

    private final EventRepository eventRepository;
    private final NotificationService notificationService;

    private static final Integer STATUS_PAST = 2;
    private static final Integer STATUS_ONGOING = 1;
    private static final Integer STATUS_ACTIVE = 0;

    /**
     * Scheduled task that updates event statuses every minute.
     * Marks events as PAST if they have ended and as ONGOING if they have started.
     * Runs every 60 seconds.
     *
     * @throws Exception if an error occurs during status update
     */
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

    /**
     * Scheduled task that sends reminder notifications for events starting within one hour.
     * Runs every 60 seconds and ensures each event receives only one reminder.
     *
     * @throws Exception if an error occurs during reminder sending
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void sendEventReminders() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderTime = now.plusHours(1);

        List<Event> eventsToRemind = eventRepository.findEventByStatusOfEventAndStartTime(STATUS_ACTIVE, reminderTime);

        for(Event event : eventsToRemind){
            if (!event.getReminderSent()) {
                 notificationService.notifyEventReminder(event.getId());
                 event.setReminderSent(true);
                 eventRepository.save(event);
            }
        }
    }

    /**
     * Scheduled task that sends rating reminders to organizers after events have ended.
     * Runs every 60 seconds and sends reminders 30 minutes after event completion.
     *
     * @throws Exception if an error occurs during reminder sending
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void sendRatingReminders() throws Exception{
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime ratingReminderTime = now.minusMinutes(30);

        List<Event> eventsToBeRated = eventRepository.findEventByStatusOfEventAndEndTime(STATUS_PAST, ratingReminderTime);

        for(Event event : eventsToBeRated){
            if(!event.getRated()){
                notificationService.notifyRateParticipants(event.getId(), event.getOrganizer().getId());
                event.setRated(true);
                eventRepository.save(event);
            }
        }
    }

    /**
     * Updates events that have ended to PAST status.
     * Private helper method called by the scheduled status update task.
     *
     * @param now the current timestamp
     * @return the number of events updated to PAST status
     */
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
    
    /**
     * Updates events that have started to ONGOING status.
     * Private helper method called by the scheduled status update task.
     *
     * @param now the current timestamp
     * @return the number of events updated to ONGOING status
     */
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
