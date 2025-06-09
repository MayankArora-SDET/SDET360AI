package com.sdet.sdet360.tenant.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PromptRequest {
    private String testCaseId;
    private String url;
    private String category;
    private List<PromptStep> steps;
}