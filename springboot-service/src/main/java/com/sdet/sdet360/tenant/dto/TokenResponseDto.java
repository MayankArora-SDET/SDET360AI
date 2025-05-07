package com.sdet.sdet360.tenant.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for token generation responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDto {
    private String token;
    private String message;
    private boolean success;
    private LocalDateTime timestamp;

    public static TokenResponseDto success(String token) {
        return TokenResponseDto.builder()
                .token(token)
                .message("Token generated successfully")
                .success(true)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static TokenResponseDto error(String errorMessage) {
        return TokenResponseDto.builder()
                .token(null)
                .message(errorMessage)
                .success(false)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
