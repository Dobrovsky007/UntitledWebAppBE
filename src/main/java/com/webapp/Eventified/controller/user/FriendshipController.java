package com.webapp.Eventified.controller.user;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.Eventified.service.FriendshipService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/friend-request")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    @PostMapping("/send/{addresseeId}")
    public ResponseEntity<?> sendFriendRequest(Authentication authentication, @PathVariable UUID addresseeId){
        String username = authentication.getName();

        if(friendshipService.sendFriendRequest(username, addresseeId)){
            return ResponseEntity.ok("Friend request sent");
        } else{
            return ResponseEntity.status(500).body("Failed to send friend request");
        }
    }

    @PostMapping("/accept/{requesterId}")
    public ResponseEntity<?> acceptFriendRequest(Authentication authentication, @PathVariable UUID requesterId){
        String username = authentication.getName();

        if(friendshipService.acceptFriendRequest(requesterId, username)){
            return ResponseEntity.ok("Friend request accepted");
        } else{
            return ResponseEntity.status(500).body("Failed to accept friend request");
        }
    }

    @PostMapping("/decline/{requesterId}")
    public ResponseEntity<?> declineFriendRequest(Authentication authentication, @PathVariable UUID requesterId){
        String username = authentication.getName();

        if(friendshipService.declineFriendRequest(requesterId, username)){
            return ResponseEntity.ok("Friend request declined");
        } else{
            return ResponseEntity.status(500).body("Failed to decline friend request");
        }
    }
    
}
