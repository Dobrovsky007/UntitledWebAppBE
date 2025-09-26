package com.webapp.Eventified.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.webapp.Eventified.domain.SportUser;
import com.webapp.Eventified.domain.User;
import com.webapp.Eventified.dto.SportDTO;
import com.webapp.Eventified.dto.UserProfileDTO;
import com.webapp.Eventified.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileDTO getUserInfo(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalAccessError("User not found"));
                return mapToUserProfileDTO(user);
    }

    private UserProfileDTO mapToUserProfileDTO(User user){
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername(user.getUsername());
        dto.setRating(calculateRating(user));
        dto.setSports(mapToSportDTOList(user.getSports()));
        dto.setVerified(user.isVerified());

        return dto;
    }

    private float calculateRating(User user){
        if(user.getNumberOfReviews() == 0) return 0;
        return (float) user.getTrustScore() / user.getNumberOfReviews();
    }

    private List<SportDTO> mapToSportDTOList(Set<SportUser> sports){
        return sports.stream()
                .map(sport ->{
                    return new SportDTO();
                })
                .collect(Collectors.toList());
    }

}
