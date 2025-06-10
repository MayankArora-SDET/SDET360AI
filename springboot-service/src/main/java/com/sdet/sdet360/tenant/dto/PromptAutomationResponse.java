package com.sdet.sdet360.tenant.dto;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromptAutomationResponse {
    private boolean success;
    private String message;
    private String testCaseId;
    private String category;
    private String logPath;
    private String reportPath;
    private String outputPath;
}