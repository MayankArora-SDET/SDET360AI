package com.sdet.sdet360.tenant.dto;

import lombok.Data;

import java.util.List;

@Data
public class PromptAutomationTestCaseDto {
    private String testCaseId;
    private String category;
    private String description;
    private List<PromptAutomationTestStepDto> steps;
}
