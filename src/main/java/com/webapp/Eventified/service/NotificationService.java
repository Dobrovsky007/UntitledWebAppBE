package com.webapp.Eventified.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.webapp.Eventified.domain.Event;
import com.webapp.Eventified.domain.EventParticipant;
import com.webapp.Eventified.domain.Notifications;
import com.webapp.Eventified.domain.SportUser;
import com.webapp.Eventified.repository.EventParticipantRepository;
import com.webapp.Eventified.repository.NotificationRepository;
import com.webapp.Eventified.repository.SportUserRepository;
import com.webapp.Eventified.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final UserRepository userRepository;
    private final SportUserRepository sportUserRepository;

    private final Integer NEW_EVENT_RECOMMENDATION = 1;
    private final Integer EVENT_CANCELLED = 2;
    private final Integer RATE_PARTICIPANTS = 3;
    private final Integer EVENT_REMINDER = 4;
    private final Integer NEW_PLAYER_JOINED = 5;
    private final Integer EVENT_UPDATE = 6;
    private final Integer PLAYER_LEFT = 7;

    public boolean notifyUsersOfNewEvent(Event event) {
        List<SportUser> interestedUsers = sportUserRepository.findUserBySportAndSkillLevel(event.getSport(),
                event.getSkillLevel());

        for (SportUser sportUser : interestedUsers) {
            if (!sportUser.getUserId().equals(event.getOrganizer().getId())) {
                createSaveNotification(sportUser.getUserId(),
                        event.getId(),
                        NEW_EVENT_RECOMMENDATION,
                        "New Event",
                        "There was new event added you might be interested in");
            }
        }
        return true;
    }

    public boolean notifyEventCancelled(UUID eventId) {
        List<EventParticipant> participants = eventParticipantRepository.findByEventId(eventId);

        for (EventParticipant participant : participants) {
            createSaveNotification(participant.getUserId(),
                    eventId,
                    EVENT_CANCELLED,
                    "Event Cancelled",
                    "An event you were participating in has been cancelled");
        }
        return true;
    }

    public boolean notifyRateParticipants(UUID eventId) {
        List<EventParticipant> participants = eventParticipantRepository.findByEventId(eventId);

        for (EventParticipant participant : participants) {
            createSaveNotification(participant.getUserId(),
                    eventId,
                    RATE_PARTICIPANTS,
                    "Rate Participants",
                    "Please rate the participants of the event you attended");
        }
        return true;
    }

    public boolean notifyEventReminder(UUID eventId) {
        List<EventParticipant> participants = eventParticipantRepository.findByEventId(eventId);

        for (EventParticipant participant : participants) {
            createSaveNotification(participant.getUserId(),
                    eventId,
                    EVENT_REMINDER,
                    "Event Reminder",
                    "This is a reminder for the event you are participating in");
        }
        return true;
    }

    public boolean notifyNewPlayerJoined(UUID eventId, UUID organizerId, String playerUsername) {

        createSaveNotification(organizerId,
                eventId,
                NEW_PLAYER_JOINED,
                "New Player Joined",
                playerUsername + " has joined the event you are participating in");

        return true;
    }

    public boolean notifyEventUpdate(UUID eventId) {
        List<EventParticipant> participants = eventParticipantRepository.findByEventId(eventId);

        for (EventParticipant participant : participants) {
            createSaveNotification(participant.getUserId(),
                    eventId,
                    EVENT_UPDATE,
                    "Event Updated",
                    "An event you are participating in has been updated");
        }
        return true;
    }

    public boolean notifyPlayerLeft(UUID eventId, UUID organizerId, String playerUsername) {

        createSaveNotification(organizerId,
                eventId,
                PLAYER_LEFT,
                "Player Left",
                playerUsername + " has left the event you are participating in");

        return true;

    }

    private void createSaveNotification(UUID userId, UUID eventId, Integer typeOfNotification, String title, String messageOfNotification) {
        Notifications notification = new Notifications(userId, eventId, typeOfNotification, title,
                messageOfNotification);
        notificationRepository.save(notification);
    }

    
}
