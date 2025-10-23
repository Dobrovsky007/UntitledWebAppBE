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

@Slf4j
@Component
public class ContentBasedScorer {

    private static final double PREFERENCE_WEIGHT = 0.4;
    private static final double HISTORY_WEIGHT = 0.3;
    private static final double SKILL_MATCH_WEIGHT = 0.15;
    private static final double RECENCY_WEIGHT = 0.05;

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

    private double calculateEventSimilarity(Event event1, Event event2){
        double similarity = 0.0;

        if(event1.getSport().equals(event2)){
        similarity += 0.6;

        int skillDiff = Math.abs(event1.getSkillLevel() - event2.getSkillLevel());
        double skillSimilarity = Math.max(0.0, 1.0 - (skillDiff * 0.2));
        
        similarity += 0.4 * skillSimilarity;
        }
        return Math.max(0.0, Math.min(1.0, similarity));
    }

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
