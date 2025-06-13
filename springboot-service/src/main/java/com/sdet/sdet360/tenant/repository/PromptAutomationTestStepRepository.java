package com.sdet.sdet360.tenant.repository;

import com.sdet.sdet360.tenant.model.PromptAutomationTestStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PromptAutomationTestStepRepository extends JpaRepository<PromptAutomationTestStep, UUID> {
    List<PromptAutomationTestStep> findByTestCase_Id(UUID testCaseId);
}
