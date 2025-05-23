package com.sdet.sdet360.tenant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "automation_prompt_based_automation")
public class AutomationPromptBasedAutomation extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "feature_id")
    private Feature feature;

    @Column(name = "user_prompt", columnDefinition = "text")
    private String userPrompt;

    @Column(name = "generated_response", columnDefinition = "text")
    private String generatedResponse;
}