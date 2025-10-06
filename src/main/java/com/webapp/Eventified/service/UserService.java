package com.webapp.Eventified.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.webapp.Eventified.domain.EventParticipant;
import com.webapp.Eventified.domain.SportUser;
import com.webapp.Eventified.domain.User;
import com.webapp.Eventified.dto.SportDTO;
import com.webapp.Eventified.dto.UserProfileDTO;
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
     * @return UserProfileDTO containing the user's profile information including username, rating, sports, and verification status
     * @throws IllegalAccessError if the user with the specified ID is not found in the database
     */
    public UserProfileDTO getOtherUserInfo(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalAccessError("User not found"));
                return mapToUserProfileDTO(user);
    }

    /**
     * Retrieves user profile information for a user by their username.
     * This method is typically used to get the current authenticated user's profile.
     *
     * @param username the unique username of the user whose profile is requested
     * @return UserProfileDTO containing the user's profile information including username, rating, sports, and verification status
     * @throws IllegalAccessError if the user with the specified username is not found in the database
     */
    public UserProfileDTO getUserInfoByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalAccessError("User not found"));
                return mapToUserProfileDTO(user);
    }

    /**
     * Maps a User entity to a UserProfileDTO for API response.
     * This method converts internal user data to a format suitable for client consumption,
     * including calculated rating and mapped sports information.
     *
     * @param user the User entity to be mapped
     * @return UserProfileDTO containing the mapped user profile data
     */
    private UserProfileDTO mapToUserProfileDTO(User user){
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername(user.getUsername());
        dto.setRating(calculateRating(user));
        dto.setSports(mapToSportDTOList(user.getSports()));
        dto.setVerified(user.isVerified());

        return dto;
    }

    /**
     * Calculates the user's average rating based on their trust score and number of reviews.
     * If the user has no reviews, returns 0 to avoid division by zero.
     *
     * @param user the User entity whose rating is to be calculated
     * @return the calculated average rating as a float, or 0 if no reviews exist
     */
    private float calculateRating(User user){
        if(user.getNumberOfReviews() == 0) return 0;
        return (float) user.getTrustScore() / user.getNumberOfReviews();
    }

    /**
     * Maps a set of SportUser entities to a list of SportDTO objects for API response.
     * Converts internal sport-user relationship data to a format suitable for client consumption.
     * 
     * @param sports the set of SportUser entities associated with a user
     * @return List of SportDTO objects representing the user's sports
     */
    private List<SportDTO> mapToSportDTOList(Set<SportUser> sports){
        return sports.stream()
                .map(sport ->{
                    return new SportDTO();
                })
                .collect(Collectors.toList());
    }

    public boolean joinEvent(String username, UUID eventId){


        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if(eventParticipantRepository.findByUserIdAndEventId(user.getId(), eventId).isPresent()){
            return false; 
        }     

        EventParticipant participant = new EventParticipant(user.getId(), eventId);
        eventParticipantRepository.save(participant);
        return true;
    }

    public boolean deleteUser(String username){
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        userRepository.delete(user);
        return true;  
    }

    public SportUser addPreferredSport(String username, SportDTO sportRequest){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        SportUser sportUser = new SportUser(user.getId(), sportRequest.getSport(), sportRequest.getSkillLevel());
        return sportUserRepository.save(sportUser);
    }
}
