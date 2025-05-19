package com.sdet.sdet360.tenant.repository;

import com.sdet.sdet360.tenant.model.AutomationPromptBasedAutomation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PromptBasedAutomationRepository extends JpaRepository<AutomationPromptBasedAutomation, UUID>{
}
