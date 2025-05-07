package com.sdet.sdet360.tenant.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


/**
 * Data Transfer Object for token generation requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequestDto {

    @NotBlank(message = "URL is required")
    private String url;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Test case ID is required")
    private String testCaseId;
}
