package com.webapp.Eventified.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.Eventified.service.UserService;

/**
 * REST controller for admin user management endpoints.
 * Provides administrative operations for managing users.
 * Access restricted to users with ADMIN role.
 *
 * @author Eventified Team
 * @version 1.0
 */
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    /**
     * Constructs a new AdminUserController with the specified UserService.
     *
     * @param userService the service layer for user-related operations
     */
    public AdminUserController(UserService userService){
        this.userService = userService;
    }

    /**
     * Retrieves detailed information for all users in the system.
     * Admin-only endpoint that returns comprehensive user data.
     *
     * @return ResponseEntity containing all users' administrative information or error if none found
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(){
        if (userService.getAllUserInfoAdmin().isEmpty()) {
            return ResponseEntity.status(500).body("No users found");
        } else {
            return ResponseEntity.ok(userService.getAllUserInfoAdmin());
        }
    }
}
