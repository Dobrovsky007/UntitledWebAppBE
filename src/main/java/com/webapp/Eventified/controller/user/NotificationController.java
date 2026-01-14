package com.webapp.Eventified.controller.user;

import org.springframework.security.core.Authentication;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.Eventified.service.NotificationService;

import lombok.RequiredArgsConstructor;

/**
 * REST controller for notification-related endpoints.
 * Provides API endpoints for managing user notifications.
 *
 * @author Eventified Team
 * @version 1.0
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Retrieves all notifications for the authenticated user.
     *
     * @param authetication the Spring Security authentication object containing user credentials
     * @return ResponseEntity containing all user notifications or error if none found
     */
    @GetMapping("/all")
    public ResponseEntity<?> getUserNotifications(Authentication authetication){
        String username = authetication.getName();
        if(notificationService.getUserNotifications(username).isEmpty()){
            return ResponseEntity.status(500).body("No notifications found for user");
        } else{
            return ResponseEntity.ok(notificationService.getUserNotifications(username));
        }
    }

    /**
     * Retrieves only unread notifications for the authenticated user.
     *
     * @param authetication the Spring Security authentication object containing user credentials
     * @return ResponseEntity containing unread notifications or error if none found
     */
    @GetMapping("/unread")
    public ResponseEntity<?> getUserUnreadNotifications(Authentication authetication){
        String username = authetication.getName();

        if(notificationService.getUnreadUserNotifications(username).isEmpty()){
            return ResponseEntity.status(500).body("No unread notifications found for user");
        } else{
            return ResponseEntity.ok(notificationService.getUnreadUserNotifications(username));
        }
    }

    /**
     * Gets the count of unread notifications for the authenticated user.
     *
     * @param authentication the Spring Security authentication object containing user credentials
     * @return ResponseEntity containing the count of unread notifications
     */
    @GetMapping("/count")
    public ResponseEntity<?> getUnreadCount(Authentication authentication){
        String username = authentication.getName();
        
        return ResponseEntity.ok(notificationService.getUnreadCount(username));
    }

    /**
     * Marks a specific notification as read.
     *
     * @param id the unique identifier of the notification to mark as read
     * @return ResponseEntity with success message if marked, or error if operation failed
     */
    @PutMapping("/read/{id}")
    public ResponseEntity<?> markAsRead(@PathVariable UUID id){
        if(notificationService.markAsRead(id)){
            return ResponseEntity.ok("Notification marked as read");
        } else{
            return ResponseEntity.status(500).body("Failed to mark notification as read");
        }
    }

    /**
     * Marks all notifications for the authenticated user as read.
     *
     * @param authentication the Spring Security authentication object containing user credentials
     * @return ResponseEntity with success message if all marked, or error if operation failed
     */
    @PutMapping("/read/all")
    public ResponseEntity<?> markAllAsRead(Authentication authentication){
        String username = authentication.getName();

        if(notificationService.markAllAsRead(username)){
            return ResponseEntity.ok("All notifications were marked as read");
        }else{
            return ResponseEntity.status(500).body("Failed to mark all notifications as read");
        }
    }

    
}
