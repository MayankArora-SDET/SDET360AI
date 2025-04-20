package com.sdet.sdet360.tenant.controller;

import com.sdet.sdet360.tenant.model.User;
import com.sdet.sdet360.tenant.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("GET /api/users called");
        List<User> users = userService.findAllUsers();
        log.debug("Fetched {} users", users.size());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        log.info("GET /api/users/{} called", id);
        return userService.findUserById(id)
                .map(user -> {
                    log.debug("User found: {}", user.getUsername());
                    return new ResponseEntity<>(user, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    log.warn("User with id {} not found", id);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @PostMapping("/tenant/{tenantId}")
    public ResponseEntity<User> createUser(
    		@PathVariable("tenantId") UUID tenantId,
            @RequestBody User user) {
        log.info("POST /api/users/tenant/{} called with payload: username={}, email={}",
                tenantId, user.getUsername(), user.getEmail());

        User newUser = userService.createUser(user);
        log.info("Created user {} with id {}", newUser.getUsername());
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable UUID id,
            @RequestBody User user) {
        log.info("PUT /api/users/{} called with payload: {}", id, user);
        return userService.findUserById(id)
                .map(existingUser -> {
                    User updatedUser = userService.updateUser(user);
                    log.info("Updated user {} (id={})", updatedUser.getUsername(), id);
                    return new ResponseEntity<>(updatedUser, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    log.warn("Cannot update—user with id {} not found", id);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.info("DELETE /api/users/{} called", id);
        return userService.findUserById(id)
                .map(user -> {
                    userService.deleteUser(id);
                    log.info("Deleted user with id {}", id);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> {
                    log.warn("Cannot delete—user with id {} not found", id);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }
}
