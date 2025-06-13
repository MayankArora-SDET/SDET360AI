package com.sdet.sdet360.tenant.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "prompt_automation_test_steps")
public class PromptAutomationTestStep extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "test_case_id")
    private PromptAutomationTestCase testCase;

    @Column(name = "step_number")
    private int stepNumber;

    @Column(name = "test_steps", columnDefinition = "text")
    private String testStep;

    @Column(name = "test_data", columnDefinition = "text")
    private String testData;

    @Column(name = "expected_result", columnDefinition = "text")
    private String expectedResult;
}
