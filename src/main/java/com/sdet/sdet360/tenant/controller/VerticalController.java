package com.sdet.sdet360.tenant.controller;

import com.sdet.sdet360.tenant.model.Vertical;
import com.sdet.sdet360.tenant.service.VerticalService;
import com.sdet.sdet360.tenant.auth.TenantAwareUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/verticals")
public class VerticalController {

    private static final Logger log = LoggerFactory.getLogger(VerticalController.class);

    @Autowired
    private VerticalService verticalService;
    
    @GetMapping
    public ResponseEntity<List<Vertical>> getAllVerticals() {
        log.info("User [{}] called GET /api/verticals");
        List<Vertical> verticals = verticalService.getAllVerticals();
        return ResponseEntity.ok(verticals);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Vertical> getVerticalById(
            @PathVariable(value = "id") UUID verticalId,
            @AuthenticationPrincipal TenantAwareUserDetails currentUser) {
        log.info("User [{}] called GET /api/verticals/{}", currentUser.getId(), verticalId);
        Vertical vertical = verticalService.getVerticalByIdAndUserId(verticalId, currentUser.getId());
        return ResponseEntity.ok(vertical);
    }
    
    @PostMapping
    public ResponseEntity<Vertical> createVertical(
            @RequestBody Vertical vertical,
            @AuthenticationPrincipal TenantAwareUserDetails currentUser) {
        log.info("User [{}] called POST /api/verticals with payload: name={}, jiraServerUrl={}",
                currentUser.getId(), vertical.getName(), vertical.getJiraServerUrl());
        Vertical savedVertical = verticalService.createVertical(vertical, currentUser.getId());
        log.info("User [{}] created Vertical [{}]", currentUser.getId(), savedVertical.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVertical);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Vertical> updateVertical(
            @PathVariable(value = "id") UUID verticalId,
            @RequestBody Vertical vertical,
            @AuthenticationPrincipal TenantAwareUserDetails currentUser) {
        log.info("User [{}] called PUT /api/verticals/{} with payload: name={}, jiraServerUrl={}",
                currentUser.getId(), verticalId, vertical.getName(), vertical.getJiraServerUrl());
        Vertical updatedVertical = verticalService.updateVertical(verticalId, vertical, currentUser.getId());
        log.info("User [{}] updated Vertical [{}]", currentUser.getId(), verticalId);
        return ResponseEntity.ok(updatedVertical);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVertical(
            @PathVariable(value = "id") UUID verticalId,
            @AuthenticationPrincipal TenantAwareUserDetails currentUser) {
        log.info("User [{}] called DELETE /api/verticals/{}", currentUser.getId(), verticalId);
        verticalService.deleteVertical(verticalId, currentUser.getId());
        log.info("User [{}] deleted Vertical [{}]", currentUser.getId(), verticalId);
        return ResponseEntity.ok().build();
    }
}
