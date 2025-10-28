package com.webapp.Eventified.service.recommendation;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.webapp.Eventified.domain.Event;
import com.webapp.Eventified.domain.SportUser;
import com.webapp.Eventified.domain.User;

import lombok.extern.slf4j.Slf4j;

/**
 * Component responsible for calculating content-based recommendation scores for events.
 * Uses a weighted scoring system based on user preferences, history, skill matching, and event recency.
 * 
 * @author Eventified Team
 * @version 1.0
 */
@Slf4j
@Component
public class ContentBasedScorer {

    private static final double PREFERENCE_WEIGHT = 0.4;
    private static final double HISTORY_WEIGHT = 0.3;
    private static final double SKILL_MATCH_WEIGHT = 0.15;
    private static final double RECENCY_WEIGHT = 0.05;

    /**
     * Calculates a comprehensive recommendation score for an event based on user preferences and history.
     * Combines multiple scoring factors with weighted importance to generate final recommendation score.
     * 
     * @param event The event to score
     * @param user The user for whom to calculate the score
     * @param attendedEvents List of events the user has previously attended
     * @return A score between 0.0 and 1.0 indicating recommendation strength
     */
    public double calculateScore(Event event, User user, List<Event> attendedEvents){

        double preferenceScore = calculatePreferenceScore(event, user);
        double historyScore = calculateHistoryScore(event, attendedEvents);
        double skillScore = calculateEventSimilarity(event, user);
        double recencyScore = calculateRecencyScore(event);

        double finalScore = (preferenceScore * PREFERENCE_WEIGHT) +
                            (historyScore * HISTORY_WEIGHT) +
                            (skillScore * SKILL_MATCH_WEIGHT) +
                            (recencyScore * RECENCY_WEIGHT);

        log.debug("Scores for Event ID {}: Preference: {}, History: {}, Skill: {}, Recency: {}, Final: {}",
                  event.getId(), preferenceScore, historyScore, skillScore, recencyScore, finalScore);    

        return finalScore;
    }

    /**
     * Calculates a score based on how well the event matches the user's sport preferences and skill level.
     * Returns 0 if user has no sports preferences or if the event's sport is not in user's preferences.
     * For matching sports, applies a skill level bonus based on how close the user's skill matches the event's required skill.
     * 
     * @param event The event to evaluate
     * @param user The user whose preferences to match
     * @return A score between 0.0 and 1.0, with 1.0 being a perfect match
     */
    private double calculatePreferenceScore(Event event, User user){
        Set<SportUser> userSports = user.getSports();

        if(userSports == null || userSports.isEmpty()){
            return 0.0;
        }

        Optional<SportUser> matchingSport = userSports.stream()
            .filter(sportUser -> sportUser.getSport().equals(event.getSport()))
            .findFirst();

        if(matchingSport.isPresent()){
            double score = 1.0;

            int skillDifference = Math.abs(matchingSport.get().getSkillLevel() - event.getSkillLevel());

            double skillBonus = Math.max(0.0, 1.0 - (skillDifference * 0.1));

            return score * skillBonus;

        } else {
            return 0.0;
        }
    }
    
    /**
     * Calculates a score based on similarity to events the user has previously attended.
     * Returns 0.5 as default if user has no attendance history.
     * Otherwise, computes average similarity across all attended events.
     * 
     * @param event The event to evaluate
     * @param attendedEvents List of events the user has attended
     * @return A score between 0.0 and 1.0 based on average similarity to attended events
     */
    private double calculateHistoryScore(Event event, List<Event> attendedEvents){
        
        if (attendedEvents == null || attendedEvents.isEmpty()){
            return 0.5;
        }

        double totalSimilarity = attendedEvents.stream()
            .mapToDouble(attendedEvent -> calculateEventSimilarity(event, attendedEvent))
            .average()
            .orElse(0.0);

        return totalSimilarity;
    }

    /**
     * Calculates similarity between two events based on sport type and skill level.
     * Awards 0.6 points for matching sport type, plus up to 0.4 points for skill level proximity.
     * 
     * @param event1 First event to compare
     * @param event2 Second event to compare
     * @return A similarity score between 0.0 and 1.0
     */
    private double calculateEventSimilarity(Event event1, Event event2){
        double similarity = 0.0;

        if(event1.getSport().equals(event2.getSport())){
        similarity += 0.6;

        int skillDiff = Math.abs(event1.getSkillLevel() - event2.getSkillLevel());
        double skillSimilarity = Math.max(0.0, 1.0 - (skillDiff * 0.2));
        
        similarity += 0.4 * skillSimilarity;
        }
        return Math.max(0.0, Math.min(1.0, similarity));
    }

    /**
     * Calculates how well an event matches a user's sports and skill levels.
     * Returns 0.5 if user has no sports, 0.3 if the event sport is not in user's preferences.
     * Otherwise calculates skill match quality based on skill level difference.
     * 
     * @param event The event to evaluate
     * @param user The user to match against
     * @return A score between 0.0 and 1.0 indicating skill match quality
     */
    private double calculateEventSimilarity(Event event, User user){
        Set<SportUser> userSports = user.getSports();

        if(userSports == null || userSports.isEmpty()){
            return 0.5;
        }

        Optional<SportUser> userSportSkill = userSports.stream()
            .filter(sport -> sport.getSport().equals(event.getSport()))
            .findFirst();

        if(userSportSkill.isEmpty()){
            return 0.3;
        }

        int skillDiff = Math.abs(userSportSkill.get().getSkillLevel() - event.getSkillLevel());

        return Math.max(0.0, 1.0 - (skillDiff * 0.2));
    }

    /**
     * Calculates a recency score based on how soon the event is happening.
     * Events happening sooner receive higher scores.
     * Awards higher scores to events within 7 days, with decreasing scores for events further in the future.
     * 
     * @param event The event to evaluate
     * @return A score between 0.2 and 1.0 based on days until the event
     */
    private double calculateRecencyScore(Event event){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime eventStartTime = event.getStartTime();
        Long daysUntilEvent = ChronoUnit.DAYS.between(now, eventStartTime);

        if(daysUntilEvent <= 7) return 1.0; 
        if(daysUntilEvent <= 14) return 0.8;
        if(daysUntilEvent <= 30) return 0.6;
        if(daysUntilEvent <= 60) return 0.4;
        
        return 0.2;

    }

}
