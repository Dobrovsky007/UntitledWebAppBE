package com.webapp.Eventified.controller.user;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.Eventified.service.RatingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    /**
     * Organizer submits ratings for all participants of an ended event.
     *
     * Body example:
     * {
     *   "alice": 5,
     *   "bob": 3
     * }
     */
    @PostMapping("/event/{eventId}")
    public ResponseEntity<?> submitEventRatings(
            @PathVariable UUID eventId,
            @RequestBody Map<String, Integer> ratingsByParticipantUsername,
            Authentication authentication) {

        try {
            String organizerUsername = authentication.getName();
            ratingService.submitEventParticipantRatings(organizerUsername, eventId, ratingsByParticipantUsername);
            return ResponseEntity.ok("Ratings submitted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
