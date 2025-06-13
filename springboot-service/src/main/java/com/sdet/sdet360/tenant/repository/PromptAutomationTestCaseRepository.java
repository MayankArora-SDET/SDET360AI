package com.sdet.sdet360.tenant.repository;

import com.sdet.sdet360.tenant.model.PromptAutomationTestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PromptAutomationTestCaseRepository extends JpaRepository<PromptAutomationTestCase, UUID> {
    Optional<PromptAutomationTestCase> findByTestCaseId(String testCaseId);
}
