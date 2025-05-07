package com.sdet.sdet360.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Data Transfer Object for token validation responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationDto {
    private boolean valid;
    private Map<String, Object> tokenData;
    private String message;
    private LocalDateTime timestamp;

    public static TokenValidationDto valid(Map<String, Object> tokenData) {
        return TokenValidationDto.builder()
                .valid(true)
                .tokenData(tokenData)
                .message("Token is valid")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static TokenValidationDto invalid(String reason) {
        return TokenValidationDto.builder()
                .valid(false)
                .tokenData(null)
                .message("Token is invalid: " + reason)
                .timestamp(LocalDateTime.now())
                .build();
    }
}