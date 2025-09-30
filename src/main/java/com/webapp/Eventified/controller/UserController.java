package com.webapp.Eventified.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.webapp.Eventified.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    /**
     * Constructs a new UserController with the specified UserService.
     *
     * @param userService the service layer for user-related operations
     */
    public UserController(UserService userService){
        this.userService = userService;
    }

    /**
     * Retrieves the profile information of the currently authenticated user.
     * Extracts the username from the JWT authentication token and returns the user's profile data.
     *
     * @param authentication the Spring Security authentication object containing user credentials
     * @return ResponseEntity containing the current user's profile information
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUserProfile(Authentication authentication){
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getUserInfoByUsername(username));
    }

    /**
     * Retrieves the profile information of a specific user by their unique identifier.
     * This endpoint allows viewing other users' profiles. The authentication parameter
     * can be used for authorization checks if needed in the future.
     *
     * @param userId the unique identifier of the user whose profile is requested
     * @param authentication the Spring Security authentication object (for potential authorization)
     * @return ResponseEntity containing the requested user's profile information
     */
    @GetMapping("/info/{userId}")
    public ResponseEntity<?> getOtherUserInfo(@PathVariable UUID userId){
        return ResponseEntity.ok(userService.getOtherUserInfo(userId));
    }
    
    @PostMapping("/event/join")
    public ResponseEntity<?> joinEvent(@RequestParam UUID eventId, Authentication authentication){
        String username = authentication.getName();
        if (userService.joinEvent(username, eventId)) {
            return ResponseEntity.ok("Successfully joined the event.");
        } else {
            return ResponseEntity.badRequest().body("User has already joined the event.");
        }
    }
}
