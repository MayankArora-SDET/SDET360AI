package com.sdet.sdet360.tenant.service;

import com.sdet.sdet360.tenant.model.Vertical;
import com.sdet.sdet360.tenant.model.User;
import com.sdet.sdet360.tenant.repository.VerticalRepository;
import com.sdet.sdet360.tenant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class VerticalService {

    @Autowired
    private VerticalRepository verticalRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Vertical> getAllVerticals() {
        return verticalRepository.findAllActive();
    }
    
    public List<Vertical> getVerticalsByUser(UUID userId) {
        return verticalRepository.findActiveByUserId(userId);
    }
    
    public Vertical getVerticalById(UUID id) {
        return verticalRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Vertical not found with id: " + id));
    }
    
    public Vertical getVerticalByIdAndUserId(UUID id, UUID userId) {
        return verticalRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new RuntimeException("Vertical not found with id: " + id + " for user id: " + userId));
    }
    
    @Transactional
    public Vertical createVertical(Vertical vertical, UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        vertical.setUser(user);
        return verticalRepository.save(vertical);
    }
    
    @Transactional
    public Vertical updateVertical(UUID id, Vertical verticalDetails, UUID userId) {
        Vertical vertical = getVerticalByIdAndUserId(id, userId);
        
        vertical.setName(verticalDetails.getName());
        vertical.setApiKey(verticalDetails.getApiKey());
        vertical.setJiraUsername(verticalDetails.getJiraUsername());
        vertical.setJiraServerUrl(verticalDetails.getJiraServerUrl());
        
        return verticalRepository.save(vertical);
    }
    
    @Transactional
    public void deleteVertical(UUID id, UUID userId) {
        Vertical vertical = getVerticalByIdAndUserId(id, userId);
        // Soft delete
        vertical.setDeletedAt(LocalDateTime.now());
        verticalRepository.save(vertical);
    }
    
    // Hard delete if needed
    @Transactional
    public void hardDeleteVertical(UUID id, UUID userId) {
        Vertical vertical = getVerticalByIdAndUserId(id, userId);
        verticalRepository.delete(vertical);
    }
}