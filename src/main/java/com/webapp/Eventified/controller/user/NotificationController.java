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

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> getUserNotifications(Authentication authetication){
        String username = authetication.getName();
        if(notificationService.getUserNotifications(username).isEmpty()){
            return ResponseEntity.status(500).body("No notifications found for user");
        } else{
            return ResponseEntity.ok(notificationService.getUserNotifications(username));
        }
    }

    @GetMapping("/unread")
    public ResponseEntity<?> getUserUnreadNotifications(Authentication authetication){
        String username = authetication.getName();

        if(notificationService.getUnreadUserNotifications(username).isEmpty()){
            return ResponseEntity.status(500).body("No unread notifications found for user");
        } else{
            return ResponseEntity.ok(notificationService.getUnreadUserNotifications(username));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> getUnreadCount(Authentication authentication){
        String username = authentication.getName();
        
        return ResponseEntity.ok(notificationService.getUnreadCount(username));
    }

    @PutMapping("/read/{id}")
    public ResponseEntity<?> markAsRead(@PathVariable UUID id){
        if(notificationService.markAsRead(id)){
            return ResponseEntity.ok("Notification marked as read");
        } else{
            return ResponseEntity.status(500).body("Failed to mark notification as read");
        }
    }

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
