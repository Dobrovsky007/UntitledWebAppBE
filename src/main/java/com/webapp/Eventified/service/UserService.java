package com.webapp.Eventified.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.webapp.Eventified.domain.EventParticipant;
import com.webapp.Eventified.domain.SportUser;
import com.webapp.Eventified.domain.User;
import com.webapp.Eventified.dto.admin.UserInfoAdmin;
import com.webapp.Eventified.dto.user.SportDTO;
import com.webapp.Eventified.dto.user.UserProfileDTO;
import com.webapp.Eventified.repository.EventParticipantRepository;
import com.webapp.Eventified.repository.SportUserRepository;
import com.webapp.Eventified.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final SportUserRepository sportUserRepository;

    /**
     * Retrieves user profile information for a specific user by their ID.
     * This method is typically used to view other users' profiles.
     *
     * @param userId the unique identifier of the user whose profile is requested
     * @return UserProfileDTO containing the user's profile information including
     *         username, rating, sports, and verification status
     * @throws IllegalAccessError if the user with the specified ID is not found in
     *                            the database
     */
    public UserProfileDTO getOtherUserInfo(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalAccessError("User not found"));
        return mapToUserProfileDTO(user);
    }

    /**
     * Retrieves user profile information for a user by their username.
     * This method is typically used to get the current authenticated user's
     * profile.
     *
     * @param username the unique username of the user whose profile is requested
     * @return UserProfileDTO containing the user's profile information including
     *         username, rating, sports, and verification status
     * @throws IllegalAccessError if the user with the specified username is not
     *                            found in the database
     */
    public UserProfileDTO getUserInfoByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalAccessError("User not found"));
        return mapToUserProfileDTO(user);
    }

    /**
     * Maps a User entity to a UserProfileDTO for API response.
     * This method converts internal user data to a format suitable for client
     * consumption,
     * including calculated rating and mapped sports information.
     *
     * @param user the User entity to be mapped
     * @return UserProfileDTO containing the mapped user profile data
     */
    private UserProfileDTO mapToUserProfileDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername(user.getUsername());
        dto.setRating(calculateRating(user));
        dto.setSports(mapToSportDTOList(user.getSports()));
        dto.setVerified(user.isVerified());

        return dto;
    }

    /**
     * Calculates the user's average rating based on their trust score and number of
     * reviews.
     * If the user has no reviews, returns 0 to avoid division by zero.
     *
     * @param user the User entity whose rating is to be calculated
     * @return the calculated average rating as a float, or 0 if no reviews exist
     */
    private float calculateRating(User user) {
        if (user.getNumberOfReviews() == 0)
            return 0;
        return (float) user.getTrustScore() / user.getNumberOfReviews();
    }

    /**
     * Maps a set of SportUser entities to a list of SportDTO objects for API
     * response.
     * Converts internal sport-user relationship data to a format suitable for
     * client consumption.
     * 
     * @param sports the set of SportUser entities associated with a user
     * @return List of SportDTO objects representing the user's sports
     */
    private List<SportDTO> mapToSportDTOList(Set<SportUser> sports) {
        return sports.stream()
                .map(sport -> {
                    SportDTO dto = new SportDTO();
                    dto.setSport(sport.getSport());
                    dto.setSkillLevel(sport.getSkillLevel());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Allows a user to join an event by creating an EventParticipant record.
     * Validates that the user exists and hasn't already joined the event.
     * Sets the participant role to 1 (regular participant) and records the join
     * timestamp.
     *
     * @param username the username of the user who wants to join the event
     * @param eventId  the unique identifier of the event to join
     * @return boolean true if the user successfully joined the event, false if
     *         already joined
     * @throws IllegalArgumentException if the user with the specified username is
     *                                  not found
     */
    public boolean joinEvent(String username, UUID eventId) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (eventParticipantRepository.findByUserIdAndEventId(user.getId(), eventId).isPresent()) {
            return false;
        }

        EventParticipant participant = new EventParticipant(user.getId(), eventId);
        eventParticipantRepository.save(participant);
        return true;
    }

    /**
     * Permanently deletes a user account and all associated data from the system.
     * This operation cascades to remove all related records including sports
     * preferences
     * and event participations due to database foreign key constraints.
     *
     * @param username the username of the user account to be deleted
     * @return boolean true if the user was successfully deleted
     * @throws IllegalArgumentException if the user with the specified username is
     *                                  not found
     */
    public boolean deleteUser(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        userRepository.delete(user);
        return true;
    }

    /**
     * Adds a new sport preference to a user's profile with the specified skill
     * level.
     * Creates a SportUser entity linking the user to their preferred sport and
     * expertise level.
     *
     * @param username     the username of the user adding the sport preference
     * @param sportRequest the sport details including sport ID and skill level
     * @return SportUser the created sport preference entity
     * @throws IllegalArgumentException if the user with the specified username is
     *                                  not found
     */
    public SportUser addPreferredSport(String username, Integer sport, Integer skillLevel) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        SportUser sportUser = new SportUser(user.getId(), sport, skillLevel);
        return sportUserRepository.save(sportUser);
    }

    /**
     * Removes a sport preference from a user's profile.
     * Deletes the SportUser entity that links the user to the specified sport.
     *
     * @param username     the username of the user removing the sport preference
     * @param sportRequest the sport details to be removed from user's preferences
     * @throws IllegalArgumentException if the user is not found or the sport
     *                                  preference doesn't exist
     */
    public void removePreferredSport(String username, SportDTO sportRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        SportUser sportUser = sportUserRepository.findByUserIdAndSport(user.getId(), sportRequest.getSport())
                .orElseThrow(() -> new IllegalArgumentException("Sport not found for user"));

        sportUserRepository.delete(sportUser);
    }

    public List<UserInfoAdmin> getAllUserInfoAdmin() {
        List<User> users = userRepository.findAll();

        List<UserInfoAdmin> userInfoAdmins = users.stream().map(user -> {
            UserInfoAdmin dto = new UserInfoAdmin();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setIsVerified(user.isVerified());
            dto.setIsAdmin(user.isAdmin());
            dto.setTrustScore(calculateRating(user));
            return dto;
        }).collect(Collectors.toList());
        return userInfoAdmins;
    }
}
