package com.sdet.sdet360.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing the contents of a token
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDataDto {
    private String username;
    private String url;
    private String description;
    private String testCaseId;
    private String vertical;
    private String category;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
}
