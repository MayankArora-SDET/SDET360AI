package com.sdet.sdet360.tenant.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromptStep {
    private String testStep;
    private String testData;
    private String expectedResult;
}
