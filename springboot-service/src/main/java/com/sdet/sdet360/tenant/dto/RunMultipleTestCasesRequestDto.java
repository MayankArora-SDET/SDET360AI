package com.sdet.sdet360.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RunMultipleTestCasesRequestDto {
    private List<String> testCaseIds;
}
