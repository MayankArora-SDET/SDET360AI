package com.sdet.sdet360.tenant.dto;

import lombok.Data;

@Data
public class PromptAutomationTestStepDto {
    private int stepNumber;
    private String testStep;
    private String testData;
    private String expectedResult;
}
