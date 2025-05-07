package com.sdet.sdet360.tenant.exception;

import com.sdet.sdet360.tenant.dto.TokenResponseDto;
import com.sdet.sdet360.tenant.dto.TokenValidationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

/**
 * Global exception handler for the application
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TokenGenerationException.class)
    public ResponseEntity<TokenResponseDto> handleTokenGenerationException(TokenGenerationException ex) {
        logger.error("Token generation failed", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(TokenResponseDto.error(ex.getMessage()));
    }

    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<TokenValidationDto> handleTokenValidationException(TokenValidationException ex) {
        logger.error("Token validation failed", ex);
        return ResponseEntity.ok(TokenValidationDto.invalid(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<TokenResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        logger.error("Validation error: {}", errorMessage);
        return ResponseEntity.badRequest().body(TokenResponseDto.error(errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<TokenResponseDto> handleGenericException(Exception ex) {
        logger.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(TokenResponseDto.error("An unexpected error occurred: " + ex.getMessage()));
    }
}