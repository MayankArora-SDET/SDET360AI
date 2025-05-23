package com.sdet.sdet360.tenant.controller;

import com.sdet.sdet360.config.annotation.SafeUUID;
import com.sdet.sdet360.tenant.dto.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import com.sdet.sdet360.tenant.exception.TokenGenerationException;
import com.sdet.sdet360.tenant.exception.TokenValidationException;
import com.sdet.sdet360.tenant.service.CodelessAutomation;
import com.sdet.sdet360.tenant.service.SeleniumTestExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public ResponseEntity<TokenResponseDto> generateToken(@PathVariable @SafeUUID UUID verticalId, @Valid @RequestBody TokenRequestDto request)
            throws TokenGenerationException {
        logger.info("Token generation request received for URL: {}", request.getUrl());
        String token = codelessAutomation.generateToken(verticalId, request);
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
            @PathVariable @SafeUUID UUID verticalId, @RequestParam(required = false) String category) {

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
    @PutMapping("/{verticalId}/update_test_case_category")
    public ResponseEntity<Map<String, String>> updateTestCaseCategory(
            @PathVariable @SafeUUID UUID verticalId,
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
            @PathVariable @SafeUUID UUID verticalId,
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
     * Get screenshots for a test case
     *
     * @param testCaseId Test case ID
     * @return List of screenshot URLs
     */
    @GetMapping("/captured_screenshots/{testCaseId}")
    public ResponseEntity<?> getScreenshots(@PathVariable String testCaseId) {
        logger.info("Controller: Retrieving screenshots for test case ID: {}", testCaseId);
        
        try {
            // Define the screenshots directory based on the same path used in SeleniumTestExecutor
            String screenshotsBaseDir = System.getProperty("user.home") + "/sdet360/screenshots";
            Path testCaseFolder = Paths.get(screenshotsBaseDir, testCaseId);
            
            if (!Files.exists(testCaseFolder)) {
                return ResponseEntity.ok(Map.of("error", "No screenshots found for this test case"));
            }
            
            // Get all PNG files in the directory
            List<String> screenshotFiles = Files.list(testCaseFolder)
                    .filter(path -> path.toString().endsWith(".png"))
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
            
            if (screenshotFiles.isEmpty()) {
                return ResponseEntity.ok(Map.of("error", "No screenshots found for this test case"));
            }
            
            // Prepare the list of screenshot URLs
            List<String> screenshots = new ArrayList<>();
            for (String filename : screenshotFiles) {
                String screenshotUrl = "/static/screenshots/" + testCaseId + "/" + filename;
                screenshots.add(screenshotUrl);
            }
            
            return ResponseEntity.ok(Map.of("screenshots", screenshots));
        } catch (IOException e) {
            logger.error("Error retrieving screenshots: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to retrieve screenshots: " + e.getMessage()));
        }
    }
    
    /**
     * Serve screenshot images
     *
     * @param testCaseId Test case ID
     * @param filename Screenshot filename
     * @return The screenshot image file
     */
    @GetMapping(value = "/static/screenshots/{testCaseId}/{filename}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Resource> serveScreenshot(
            @PathVariable String testCaseId,
            @PathVariable String filename) {
        
        logger.info("Controller: Serving screenshot {} for test case ID: {}", filename, testCaseId);
        
        try {
            logger.info("Controller: Serving screenshot from {} name: {} for test case ID: {}", System.getProperty("user.home"), filename, testCaseId);

            String screenshotsBaseDir = System.getProperty("user.home") + "/sdet360/screenshots";
            Path testCaseFolder = Paths.get(screenshotsBaseDir, testCaseId);
            Path filePath = testCaseFolder.resolve(filename);
            
            if (!Files.exists(filePath)) {
                logger.error("Screenshot file not found: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(filePath.toFile());
            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            logger.error("Error serving screenshot: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
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
            @PathVariable @SafeUUID UUID verticalId,
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
     * Update events for a test case
     *
     * @param verticalId Vertical ID
     * @param request Request containing test case ID and updated events
     * @return Success message
     */
    @PutMapping("/{verticalId}/update_event")
    public ResponseEntity<?> updateEvent(
            @PathVariable @SafeUUID UUID verticalId,
            @RequestBody UpdateEventRequestDto request) {

        logger.info("Controller: Updating events for test case ID: {}", request.getTestCaseId());

        try {
            String result = codelessAutomation.updateEvents(request.getTestCaseId(), request.getEvents());
            return ResponseEntity.ok(Map.of("message", result));
        } catch (IllegalArgumentException e) {
            logger.error("Error updating events: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error updating events: {}", e.getMessage(), e);
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

/**
 * Delete a test case
 *
 * @param request Request containing test case ID to delete
 * @return Success message
 */
@PostMapping("/delete_test_case")
public ResponseEntity<?> deleteTestCase(@RequestBody Map<String, String> request) {
    String testCaseId = request.get("testCaseId");
    logger.info("Controller: Deleting test case ID: {}", testCaseId);
    
    if (testCaseId == null || testCaseId.isEmpty()) {
        return ResponseEntity.badRequest().body(Map.of("error", "Test case ID is required"));
    }
    
    try {
        String result = codelessAutomation.deleteTestCase(testCaseId);
        return ResponseEntity.ok(Map.of("message", result));
    } catch (IllegalArgumentException e) {
        logger.error("Error deleting test case: {}", e.getMessage());
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
        logger.error("Unexpected error deleting test case: {}", e.getMessage(), e);
        return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
    }

}
}
