package com.webapp.Eventified.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.webapp.Eventified.dto.SportDTO;
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
    
    /**
     * Allows the authenticated user to join a specific event.
     * Creates an EventParticipant record linking the user to the event with participant role.
     *
     * @param eventId the unique identifier of the event to join
     * @param authentication the Spring Security authentication object containing user credentials
     * @return ResponseEntity with success message if joined successfully, or error if already joined
     */
    @PostMapping("/event/join")
    public ResponseEntity<?> joinEvent(@RequestParam UUID eventId, Authentication authentication){
        String username = authentication.getName();
        if (userService.joinEvent(username, eventId)) {
            return ResponseEntity.ok("Successfully joined the event.");
        } else {
            return ResponseEntity.badRequest().body("User has already joined the event.");
        }
    }

    /**
     * Permanently deletes the authenticated user's account and all associated data.
     * This operation cannot be undone and removes all user data from the system.
     *
     * @param authentication the Spring Security authentication object containing user credentials
     * @return ResponseEntity with success message if deletion succeeds, or error message if it fails
     */
    @DeleteMapping("/profile/delete")
    public ResponseEntity<?> deleteUser(Authentication authentication){
        String username = authentication.getName();
        if (userService.deleteUser(username)) {
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.status(500).body("Failed to delete user.");
        }
    }

    /**
     * Adds a new sport preference to the authenticated user's profile.
     * Associates the user with a sport and their skill level in that sport.
     *
     * @param sportRequest the sport details including sport ID and skill level
     * @param authentication the Spring Security authentication object containing user credentials
     * @return ResponseEntity containing the created SportUser entity or error message
     */
    @PostMapping("/sport/add")
    public ResponseEntity<?> addPreferredSport(@RequestBody SportDTO sportRequest, Authentication authentication){
        String username = authentication.getName();

        try {
            return ResponseEntity.ok(userService.addPreferredSport(username, sportRequest));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(500).body("Failed to add sport: " + e.getMessage());
        }
    }

    /**
     * Removes a sport preference from the authenticated user's profile.
     * Deletes the association between the user and the specified sport.
     *
     * @param sportRequest the sport details to be removed from user's preferences
     * @param authentication the Spring Security authentication object containing user credentials
     * @return ResponseEntity with success status or error message if removal fails
     */
    @DeleteMapping("/sport/remove")
    public ResponseEntity<?> removePreferredSport(@RequestBody SportDTO sportRequest, Authentication authentication){
        String username = authentication.getName();

        try {
            userService.removePreferredSport(username, sportRequest);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(500).body("Failed to remove sport: " + e.getMessage());
        }
    }
}
