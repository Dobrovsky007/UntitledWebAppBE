package com.webapp.Eventified.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.Authentication;

import com.webapp.Eventified.service.recommendation.RecommendationService;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    
    @GetMapping("/events")
    public ResponseEntity<?> getRecommendations(Authentication authentication, @Parameter(description = "Max number of recommendations to be shown") @RequestParam(defaultValue = "10") int limit){

        if (limit < 1) limit = 1;
        if (limit > 50) limit = 50;

        String username = authentication.getName();

        return ResponseEntity.ok(recommendationService.getRecommendedEvents(username, limit));
    }
}
