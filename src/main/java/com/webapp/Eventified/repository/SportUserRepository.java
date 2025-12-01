package com.webapp.Eventified.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webapp.Eventified.model.SportUser;
import com.webapp.Eventified.model.id.SportUserId;

/**
 * Repository interface for SportUser entity operations.
 * Provides database access methods for managing user-sport relationships.
 *
 * @author Eventified Team
 * @version 1.0
 */
public interface SportUserRepository extends JpaRepository<SportUser, SportUserId> {

    /**
     * Finds a SportUser relationship by user ID and sport.
     *
     * @param id the unique identifier of the user
     * @param sport the integer identifier of the sport
     * @return Optional containing the SportUser if found, empty otherwise
     */
    Optional<SportUser> findByUserIdAndSport(UUID id, Integer sport);
    
    /**
     * Finds all users with a specific sport and skill level combination.
     *
     * @param sport the integer identifier of the sport
     * @param skillLevel the skill level to search for
     * @return List of SportUser entities matching the criteria
     */
    List<SportUser> findUserBySportAndSkillLevel(Integer sport, Integer skillLevel);
}
