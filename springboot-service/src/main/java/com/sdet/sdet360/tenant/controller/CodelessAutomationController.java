package com.sdet.sdet360.tenant.controller;

import com.sdet.sdet360.tenant.dto.*;
import com.sdet.sdet360.tenant.exception.TokenGenerationException;
import com.sdet.sdet360.tenant.exception.TokenValidationException;
import com.sdet.sdet360.tenant.service.CodelessAutomation;
import com.sdet.sdet360.tenant.service.SeleniumTestExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for token generation and validation
 */
@RestController
@RequestMapping("/api/tokens")
public class CodelessAutomationController {

    private static final Logger logger = LoggerFactory.getLogger(CodelessAutomationController.class);
    private final CodelessAutomation codelessAutomation;
    private final SeleniumTestExecutor seleniumTestExecutor;

    public CodelessAutomationController(CodelessAutomation codelessAutomation, SeleniumTestExecutor seleniumTestExecutor) {
        this.codelessAutomation = codelessAutomation;
        this.seleniumTestExecutor = seleniumTestExecutor;
    }

    /**
     * Generates a new token based on the request parameters
     *
     * @param verticalId UUID of the vertical
     * @param request TokenRequestDto containing token parameters
     * @return ResponseEntity with TokenResponseDto
     * @throws TokenGenerationException if token generation fails
     */
    @PostMapping("/{verticalId}/generate")
    public ResponseEntity<TokenResponseDto> generateToken(@PathVariable String verticalId, @Valid @RequestBody TokenRequestDto request)
            throws TokenGenerationException {
        logger.info("Token generation request received for URL: {}", request.getUrl());
        UUID verId = UUID.fromString(verticalId);
        String token = codelessAutomation.generateToken(verId, request);
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

    /**
     * Get recorded test cases with optional category filtering
     *
     * @param verticalId Vertical ID
     * @param category Optional category filter
     * @return List of test cases with their associated events
     */
    @GetMapping("/{verticalId}/recorded-test-cases")
    public ResponseEntity<Map<String, List<TestCaseWithEventsDto>>> getRecordedTestCases(
            @PathVariable UUID verticalId, @RequestParam(required = false) String category) {

        logger.info("Controller: Retrieving test cases with category filter: {}", category);

        List<TestCaseWithEventsDto> testCasesWithEvents = codelessAutomation.getRecordedTestCases(category);

        return ResponseEntity.ok(Map.of("test_cases", testCasesWithEvents));
    }

    /**
     * Update the category of a test case
     *
     * @param verticalId Vertical ID
     * @param request Update category request containing test case ID and new category
     * @return Success message
     */
    @PutMapping("/{verticalId}/update-test-case-category")
    public ResponseEntity<Map<String, String>> updateTestCaseCategory(
            @PathVariable UUID verticalId,
            @RequestBody UpdateCategoryRequestDto request) {

        logger.info("Controller: Updating category for test case ID: {} to {}", 
                request.getTestCaseId(), request.getCategory());

        try {
            String result = codelessAutomation.updateTestCaseCategory(request.getTestCaseId(), request.getCategory());
            return ResponseEntity.ok(Map.of("message", result));
        } catch (IllegalArgumentException e) {
            logger.error("Error updating test case category: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error updating test case category: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    /**
     * Get events for a specific test case
     *
     * @param verticalId Vertical ID
     * @param testCaseId Test case ID
     * @return Test case with its events
     */
    @GetMapping("/{verticalId}/test-case-events/{testCaseId}")
    public ResponseEntity<?> getTestCaseEvents(
            @PathVariable UUID verticalId,
            @PathVariable String testCaseId) {

        logger.info("Controller: Retrieving events for test case ID: {}", testCaseId);

        try {
            TestCaseWithEventsDto testCase = codelessAutomation.getTestCaseEvents(testCaseId);
            return ResponseEntity.ok(testCase);
        } catch (IllegalArgumentException e) {
            logger.error("Error retrieving test case events: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error retrieving test case events: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    /**
     * Run multiple test cases
     *
     * @param verticalId Vertical ID
     * @param request Request containing list of test case IDs to run
     * @return Results of the test execution
     */
    @PostMapping("/{verticalId}/run-multiple-test-cases")
    public ResponseEntity<?> runMultipleTestCases(
            @PathVariable UUID verticalId,
            @RequestBody RunMultipleTestCasesRequestDto request) {

        logger.info("Controller: Running multiple test cases: {}", request.getTestCaseIds());

        try {
            List<TestExecutionResultDto> results = codelessAutomation.runMultipleTestCases(
                    request.getTestCaseIds(), 
                    seleniumTestExecutor
            );
            return ResponseEntity.ok(Map.of("results", results));
        } catch (Exception e) {
            logger.error("Error running multiple test cases: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }
    
    /**
     * Record interaction log with events
     *
     * @param request Interaction log request with token and events
     * @return Success message
     */
    @PostMapping("/record")
    public ResponseEntity<?> recordInteractionsLog(@RequestBody InteractionLogRequestDto request) {
        String token = request.getToken();
        List<EventDto> events = request.getEvents();
        boolean enableAssertion = request.isEnableAssertion();

        try {
            TokenDataDto tokenData = codelessAutomation.validateToken(token);

            String result = codelessAutomation.recordInteractionsLog(UUID.fromString(tokenData.getVertical()), tokenData, events, enableAssertion);

            return ResponseEntity.ok(Map.of("message", result));
        } catch (TokenValidationException e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request data: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error recording interactions: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to record interactions: " + e.getMessage()));
        }
    }
}