package com.sdet.sdet360.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestExecutionResultDto {
    private String testCaseId;
    private String status;
    private boolean autoHealed;
    private boolean assertion;
    private List<String> eventIds; // Changed from boolean eventId to List<String> eventIds
    private String errorMessage;
    private List<String> screenshots;
    private List<Map<String, Object>> executedEvents;
}
