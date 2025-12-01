package com.webapp.Eventified.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.webapp.Eventified.model.Event;
import com.webapp.Eventified.model.EventParticipant;
import com.webapp.Eventified.model.Notification;
import com.webapp.Eventified.model.SportUser;
import com.webapp.Eventified.model.User;
import com.webapp.Eventified.repository.EventParticipantRepository;
import com.webapp.Eventified.repository.NotificationRepository;
import com.webapp.Eventified.repository.SportUserRepository;
import com.webapp.Eventified.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service class for managing notification operations in the Eventified application.
 * Handles creation and distribution of notifications for various event-related activities.
 *
 * @author Eventified Team
 * @version 1.0
 */
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
    private final Integer FRIEND_REQUEST = 8;

    /**
     * Notifies users about a new event that matches their sport and skill level preferences.
     * Sends notifications to all interested users except the event organizer.
     *
     * @param event the newly created event
     * @return true if notifications were successfully sent
     */
    public boolean notifyUsersOfNewEvent(Event event) {
        List<SportUser> interestedUsers = sportUserRepository.findUserBySportAndSkillLevel(event.getSport(),
                event.getSkillLevel());

        for (SportUser sportUser : interestedUsers) {
            if (!sportUser.getUserId().equals(event.getOrganizer().getId())) {
                createSaveNotification(sportUser.getUser(),
                        event,
                        NEW_EVENT_RECOMMENDATION,
                        "New Event",
                        "There was new event added you might be interested in");
            }
        }
        return true;
    }

    /**
     * Notifies all participants that an event has been cancelled.
     *
     * @param eventId the unique identifier of the cancelled event
     * @return true if notifications were successfully sent
     */
    public boolean notifyEventCancelled(Event event) {
        List<EventParticipant> participants = eventParticipantRepository.findByEventId(event.getId());

        for (EventParticipant participant : participants) {
            createSaveNotification(participant.getUser(),
                    event,
                    EVENT_CANCELLED,
                    "Event Cancelled",
                    "An event you were participating in has been cancelled");
        }
        return true;
    }

    /**
     * Notifies the event organizer to rate participants after an event has ended.
     *
     * @param eventId the unique identifier of the event
     * @param organizerId the unique identifier of the event organizer
     * @return true if notification was successfully sent
     */
    public boolean notifyRateParticipants(Event event, User organizer) {
            createSaveNotification(organizer,
                    event,
                    RATE_PARTICIPANTS,
                    "Rate Participants",
                    "Please rate the participants of the event you attended");
        
        return true;
    }

    /**
     * Sends a reminder notification to all participants of an upcoming event.
     *
     * @param eventId the unique identifier of the event
     * @return true if notifications were successfully sent
     */
    public boolean notifyEventReminder(Event event) {
        List<EventParticipant> participants = eventParticipantRepository.findByEventId(event.getId());

        for (EventParticipant participant : participants) {
            createSaveNotification(participant.getUser(),
                    event,
                    EVENT_REMINDER,
                    "Event Reminder",
                    "This is a reminder for the event you are participating in");
        }
        return true;
    }

    /**
     * Notifies the event organizer when a new player joins their event.
     *
     * @param eventId the unique identifier of the event
     * @param organizerId the unique identifier of the event organizer
     * @param playerUsername the username of the player who joined
     * @return true if notification was successfully sent
     */
    public boolean notifyNewPlayerJoined(Event event, User organizer, String playerUsername) {

        createSaveNotification(organizer,
                event,
                NEW_PLAYER_JOINED,
                "New Player Joined",
                playerUsername + " has joined the event you are participating in");

        return true;
    }

    /**
     * Notifies all participants when event details have been updated.
     *
     * @param eventId the unique identifier of the updated event
     * @return true if notifications were successfully sent
     */
    public boolean notifyEventUpdate(Event event) {
        List<EventParticipant> participants = eventParticipantRepository.findByEventId(event.getId());

        for (EventParticipant participant : participants) {
            createSaveNotification(participant.getUser(),
                    event,
                    EVENT_UPDATE,
                    "Event Updated",
                    "An event you are participating in has been updated");
        }
        return true;
    }

    /**
     * Notifies the event organizer when a player leaves their event.
     *
     * @param eventId the unique identifier of the event
     * @param organizerId the unique identifier of the event organizer
     * @param playerUsername the username of the player who left
     * @return true if notification was successfully sent
     */
    public boolean notifyPlayerLeft(Event event, User organizer, String playerUsername) {

        createSaveNotification(organizer,
                event,
                PLAYER_LEFT,
                "Player Left",
                playerUsername + " has left the event you are participating in");

        return true;

    }

    public boolean notifyFriendRequest(User receiver, String senderUsername){

        createSaveNotification(receiver, null, FRIEND_REQUEST,
                "New Friend Request",
                "You have new friend request from " + senderUsername);

        return true;
    }

    /**
     * Creates and saves a notification to the database.
     * This is a private helper method used by all notification methods.
     *
     * @param userId the unique identifier of the user receiving the notification
     * @param eventId the unique identifier of the event related to the notification
     * @param typeOfNotification the integer identifier of the notification type
     * @param title the title/subject of the notification
     * @param messageOfNotification the detailed message content
     */
    private void createSaveNotification(User user, Event event, Integer typeOfNotification, String title, String messageOfNotification) {
        Notification notification = new Notification(user, event, typeOfNotification, title,
                messageOfNotification);
        notificationRepository.save(notification);
    }

    /**
     * Retrieves all notifications for a specific user, ordered by creation time.
     *
     * @param username the username of the user
     * @return List of all notifications for the user
     * @throws IllegalArgumentException if the user is not found
     */
    public List<Notification> getUserNotifications(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    /**
     * Retrieves only unread notifications for a specific user, ordered by creation time.
     *
     * @param username the username of the user
     * @return List of unread notifications for the user
     * @throws IllegalArgumentException if the user is not found
     */
    public List<Notification> getUnreadUserNotifications(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(user.getId(), false);
    }

    /**
     * Gets the count of unread notifications for a specific user.
     *
     * @param username the username of the user
     * @return the number of unread notifications
     * @throws IllegalArgumentException if the user is not found
     */
    public int getUnreadCount(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return notificationRepository.countByUserIdAndIsRead(user.getId(), false).intValue();
    }

    /**
     * Marks a specific notification as read.
     *
     * @param notificationId the unique identifier of the notification
     * @return true if the notification was found and marked as read, false otherwise
     */
    public boolean markAsRead(UUID notificationId){
        return notificationRepository.findById(notificationId)
                .map(notification -> {
                    notification.setIsRead(true);
                    notificationRepository.save(notification);
                    return true;
                }).orElse(false);
    }

    /**
     * Marks all notifications for a specific user as read.
     *
     * @param username the username of the user
     * @return true if all notifications were successfully marked as read
     * @throws IllegalArgumentException if the user is not found
     */
    public boolean markAllAsRead(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Notification> unreadNotifications = notificationRepository.findByUserId(user.getId());

        for(Notification notification : unreadNotifications){
            if(!notification.getIsRead()){
                notification.setIsRead(true);
                notificationRepository.save(notification);
            }
        }
        return true;
    }
}
