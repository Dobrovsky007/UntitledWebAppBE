package com.webapp.Eventified.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.webapp.Eventified.model.Friendship;
import com.webapp.Eventified.model.User;
import com.webapp.Eventified.repository.FriendshipRepository;
import com.webapp.Eventified.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public boolean sendFriendRequest(String requesterUsername, UUID addresseeId){

        User requester = userRepository.findByUsername(requesterUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + requesterUsername));
        
        User addressee = userRepository.findById(addresseeId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + addresseeId));

        Friendship friendship = new Friendship(requester, addressee);
        friendshipRepository.save(friendship);

        notificationService.notifyFriendRequest(addressee, requesterUsername);
        log.info("Friend request sent from {} to {}", requesterUsername, addresseeId);
        return true;
    }

    public boolean acceptFriendRequest(UUID requesterId, String addresseeUsername){

        User addressee = userRepository.findByUsername(addresseeUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + addresseeUsername));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + requesterId));

        Friendship friendship = friendshipRepository.findByRequesterAndAddressee(requester, addressee)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found from " + requester.getUsername() + " to " + addressee.getUsername()));

        friendship.setStatus(1);
        friendshipRepository.save(friendship);

        return true;
    }

    public boolean declineFriendRequest(UUID requesterId, String addresseeUsername){

        User addressee = userRepository.findByUsername(addresseeUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + addresseeUsername));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + requesterId));

        Friendship friendship = friendshipRepository.findByRequesterAndAddressee(requester, addressee)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found from " + requester.getUsername() + " to " + addressee.getUsername()));

        friendship.setStatus(2);
        friendshipRepository.save(friendship);

        return true;
    }
}
