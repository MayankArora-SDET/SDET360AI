package com.sdet.sdet360.tenant.controller;


import com.sdet.sdet360.tenant.dto.TokenDataDto;
import com.sdet.sdet360.tenant.dto.TokenRequestDto;
import com.sdet.sdet360.tenant.dto.TokenResponseDto;
import com.sdet.sdet360.tenant.dto.TokenValidationDto;
import com.sdet.sdet360.tenant.exception.TokenGenerationException;
import com.sdet.sdet360.tenant.exception.TokenValidationException;
import com.sdet.sdet360.tenant.service.CodelessAutomation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for token generation and validation
 */
@RestController
@RequestMapping("/api/tokens")
public class CodelessAutomationController {

    private static final Logger logger = LoggerFactory.getLogger(CodelessAutomationController.class);
    private final CodelessAutomation codelessAutomation;

    public CodelessAutomationController(CodelessAutomation codelessAutomation) {
        this.codelessAutomation = codelessAutomation;
    }

    /**
     * Generates a new token based on the request parameters
     *
     * @param request TokenRequestDto containing token parameters
     * @return ResponseEntity with TokenResponseDto
     * @throws TokenGenerationException if token generation fails
     */
    @PostMapping("/generate")
    public ResponseEntity<TokenResponseDto> generateToken(@javax.validation.Valid @RequestBody TokenRequestDto request)
            throws TokenGenerationException {
        logger.info("Token generation request received for URL: {}", request.getUrl());

        String token = codelessAutomation.generateToken(request);
        logger.debug("Token generated successfully");

        return ResponseEntity.ok(TokenResponseDto.success(token));
    }

    /**
     * Validates a token and returns its data
     *
     * @param token JWT token string
     * @return ResponseEntity with TokenValidationDto
     * @throws TokenValidationException if token validation fails
     */
    @GetMapping("/validate")
    public ResponseEntity<TokenValidationDto> validateToken(@RequestParam String token)
            throws TokenValidationException {
        logger.info("Token validation request received");

        TokenDataDto tokenData = codelessAutomation.validateToken(token);
        logger.debug("Token validated successfully for user: {}", tokenData.getUsername());

        return ResponseEntity.ok(TokenValidationDto.valid(Map.of(
                "username", tokenData.getUsername(),
                "url", tokenData.getUrl(),
                "description", tokenData.getDescription(),
                "testCaseId", tokenData.getTestCaseId(),
                "vertical", tokenData.getVertical(),
                "category", tokenData.getCategory(),
                "issuedAt", tokenData.getIssuedAt(),
                "expiresAt", tokenData.getExpiresAt()
        )));
    }
}