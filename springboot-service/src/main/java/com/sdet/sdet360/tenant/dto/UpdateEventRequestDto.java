package com.sdet.sdet360.tenant.dto;

import lombok.Data;
import java.util.List;

/**
 * DTO for updating test case events
 */
@Data
public class UpdateEventRequestDto {
    private String testCaseId;
    private List<EventDto> events;
}
