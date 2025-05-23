package com.sdet.sdet360.tenant.service;

import com.sdet.sdet360.tenant.auth.JwtProperties;
import com.sdet.sdet360.tenant.dto.*;
import com.sdet.sdet360.tenant.enums.TestCaseCategory;
import com.sdet.sdet360.tenant.exception.TokenGenerationException;
import com.sdet.sdet360.tenant.exception.TokenValidationException;
import com.sdet.sdet360.tenant.model.EventsTable;
import com.sdet.sdet360.tenant.model.Feature;
import com.sdet.sdet360.tenant.model.InteractionTable;
import com.sdet.sdet360.tenant.repository.EventsTableRepository;
import com.sdet.sdet360.tenant.repository.FeatureRepository;
import com.sdet.sdet360.tenant.repository.InteractionTableRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class CodelessAutomation {

    private static final Logger logger = LoggerFactory.getLogger(CodelessAutomation.class);

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;
    private final InteractionTableRepository interactionTableRepository;
    private final EventsTableRepository eventsTableRepository;
    private final FeatureRepository featureRepository;

    public CodelessAutomation(
            JwtProperties jwtProperties,
            SecretKey jwtSecretKey,
            InteractionTableRepository interactionTableRepository,
            EventsTableRepository eventsTableRepository,
            FeatureRepository featureRepository) {
        this.jwtProperties = jwtProperties;
        this.secretKey = jwtSecretKey;
        this.interactionTableRepository = interactionTableRepository;
        this.eventsTableRepository = eventsTableRepository;
        this.featureRepository = featureRepository;
    }

    /**
     * Generates a token based on the provided request and authenticated user
     *
     * @param verticalId Vertical ID
     * @param request TokenRequestDto containing request parameters
     * @return Generated JWT token string
     * @throws TokenGenerationException if token generation fails
     */
    public String generateToken(UUID verticalId, TokenRequestDto request) throws TokenGenerationException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                throw new TokenGenerationException("No authenticated user found");
            }

            String username = authentication.getName();
            String vertical = verticalId.toString();

            Instant now = Instant.now();
            Instant expiration = now.plusSeconds(jwtProperties.getExpirationSeconds());

            Map<String, Object> claims = new HashMap<>();
            claims.put("username", username);
            claims.put("url", request.getUrl());
            claims.put("description", request.getDescription());
            claims.put("testCaseId", request.getTestCaseId());
            claims.put("vertical", vertical);
            claims.put("category", request.getCategory());

            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuer(jwtProperties.getIssuer())
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(expiration))
                    .signWith(secretKey)
                    .compact();
        } catch (Exception e) {
            throw new TokenGenerationException("Failed to generate token: " + e.getMessage(), e);
        }
    }

    /**
     * Validates a token and returns its data
     *
     * @param token JWT token string
     * @return TokenDataDto containing token data
     * @throws TokenValidationException if token is invalid or expired
     */
    public TokenDataDto validateToken(String token) throws TokenValidationException {
        try {
            // Parse and validate token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Extract token data
            return TokenDataDto.builder()
                    .username((String) claims.get("username"))
                    .url((String) claims.get("url"))
                    .description((String) claims.get("description"))
                    .testCaseId((String) claims.get("testCaseId"))
                    .vertical((String) claims.get("vertical"))
                    .category((String) claims.get("category"))
                    .issuedAt(toLocalDateTime(claims.getIssuedAt()))
                    .expiresAt(toLocalDateTime(claims.getExpiration()))
                    .build();
        } catch (ExpiredJwtException e) {
            throw new TokenValidationException("Token has expired", e);
        } catch (SignatureException e) {
            throw new TokenValidationException("Invalid token signature", e);
        } catch (MalformedJwtException e) {
            throw new TokenValidationException("Malformed token", e);
        } catch (Exception e) {
            throw new TokenValidationException("Token validation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Get recorded test cases with optional category filtering
     *
     * @param category Optional category filter
     * @return List of test cases with their associated events
     */
    public List<TestCaseWithEventsDto> getRecordedTestCases(String category) {
        logger.info("Service: Retrieving test cases with category filter: {}", category);

        // Convert category to lowercase for case-insensitive comparison
        String categoryFilter = (category == null || category.isEmpty()) ?
                TestCaseCategory.ALL.name().toUpperCase() : category.toUpperCase();

        // Build query based on category
        List<InteractionTable> testCases;
        if (categoryFilter.equals(TestCaseCategory.ALL.name().toUpperCase())) {
            testCases = interactionTableRepository.findAllActive();
        } else {
            testCases = interactionTableRepository.findByCategoryAndNotDeleted(categoryFilter);
        }
        
        logger.info("Found {} test cases matching category: {}", testCases.size(), categoryFilter);
        
        // Map to DTOs with events
        return testCases.stream()
                .map(testCase -> {
                    // Fetch events for this test case
                    List<EventsTable> events = eventsTableRepository.findByTestcaseId(testCase.getTestcaseId());
                    logger.info("Found {} events for test case ID: {}", events.size(), testCase.getTestcaseId());
                    
                    // Convert EventsTable entities to EventDto objects
                    List<EventDto> eventDtos = events.stream()
                            .map(event -> {
                                EventDto dto = new EventDto();
                                dto.setAbsoluteXPath(event.getAbsolutePath());
                                dto.setRelativeXPath(event.getRelativeXpath());
                                dto.setRelationalXPath(event.getRelationalXpath());
                                dto.setAction(event.getAction());
                                dto.setType(event.getType());
                                dto.setValue(event.getValue());
                                return dto;
                            })
                            .collect(Collectors.toList());
                    
                    // Create and return the DTO with test case info and events
                    return new TestCaseWithEventsDto(
                            testCase.getTestcaseId().toString(),
                            testCase.getTcId(),
                            testCase.getUrl(),
                            testCase.getDescription(),
                            testCase.getCategory().toLowerCase(),
                            eventDtos);
                })
                .collect(Collectors.toList());
    }

    /**
     * Record interaction log with events
     *
     * @param verticalId Vertical ID
     * @param tokenData Validated token data
     * @param events List of events to record
     * @param enableAssertion Whether to enable assertions
     * @return Success message
     */
    @Transactional
    public String recordInteractionsLog(UUID verticalId, TokenDataDto tokenData, List<EventDto> events, boolean enableAssertion) {
        // Extract data from token
        String vertical = verticalId.toString();
        String testCaseId = tokenData.getTestCaseId();
        String description = tokenData.getDescription();
        String url = tokenData.getUrl();
        String category = tokenData.getCategory().toLowerCase();

        // Validate required fields
        if (vertical == null || url == null || testCaseId == null || description == null) {
            throw new IllegalArgumentException("Vertical, Test case Id, Description and URL must be provided in the request.");
        }

        // Get or create feature based on vertical
        Feature feature = featureRepository.findByFeatureName(vertical)
                .orElseGet(() -> {
                    Feature newFeature = new Feature();
                    newFeature.setFeatureName(vertical);
                    return featureRepository.save(newFeature);
                });

        // Find existing interaction or create new one
        UUID testCaseUuid;
        try {
            testCaseUuid = UUID.fromString(testCaseId);
        } catch (IllegalArgumentException e) {
            testCaseUuid = UUID.randomUUID(); // Generate new UUID if invalid format
        }

        UUID finalTestCaseUuid = testCaseUuid;
        InteractionTable interaction = interactionTableRepository
                .findByTestcaseId(testCaseUuid)
                .orElseGet(() -> {
                    InteractionTable newInteraction = new InteractionTable(
                            feature, finalTestCaseUuid, testCaseId, description, category, url);
                    return interactionTableRepository.save(newInteraction);
                });

        // zUpdate existing interaction if found
        interaction.setDescription(description);
        interaction.setCategory(category);
        interaction.setUrl(url);
        interactionTableRepository.save(interaction);

        // Process events
        saveEvents(events, interaction, enableAssertion);

        return String.format("Test case '%s' saved/updated successfully.", testCaseId);
    }

    /**
     * Save events for an interaction
     * @param events List of event DTOs to save
     * @param interaction The interaction table record to associate events with
     * @param enableAssertion Whether to enable assertions for these events
     */
    private void saveEvents(List<EventDto> events, InteractionTable interaction, boolean enableAssertion) {
        logger.info("Saving {} events for test case ID: {}", events.size(), interaction.getTestcaseId());
        
        // Convert and save each event
        List<EventsTable> eventEntities = events.stream().map(eventDto -> {
            EventsTable event = new EventsTable();
            event.setInteraction(interaction);
            event.setAbsolutePath(eventDto.getAbsoluteXPath());
            event.setRelativeXpath(eventDto.getRelativeXPath());
            event.setRelationalXpath(eventDto.getRelationalXPath());
            event.setAction(eventDto.getAction());
            event.setType(eventDto.getType());
            event.setValue(eventDto.getValue());
            event.setAssertion(enableAssertion && eventDto.getAssertion() != null ? eventDto.getAssertion() : false);
            event.setAssertionStatus(eventDto.getAssertionStatus());
            event.setAutoHealed(eventDto.getAutohealed());
            event.setIsModified(false); // Default value for new events
            return event;
        }).collect(Collectors.toList());
        
        // Save all events in a batch
        eventsTableRepository.saveAll(eventEntities);
        logger.info("Successfully saved {} events", eventEntities.size());
    }
    
    /**
     * Save events for an interaction
     * @param authentication Current authentication object (used as fallback)
     * @return Vertical value extracted from JWT token
     */
    private String extractVerticalFromAuthentication(Authentication authentication) {
        try {
            // Get current request from RequestContextHolder
            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            if (request != null) {
                // Get JWT token from cookies or Authorization header
                String token = extractJwtFromRequest(request);

                if (token != null && !token.isEmpty()) {
                    // Decode JWT token without validation (just to extract claims)
                    // This avoids dependency on specific JWT implementation
                    Map<String, Object> claims = extractClaims(token);

                    // Extract tenantId from claims
                    if (claims.containsKey("tenantId")) {
                        String tenantId = claims.get("tenantId").toString();
                        logger.debug("Extracted tenant ID from JWT token: {}", tenantId);
                        return tenantId;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to extract tenant ID from JWT token: {}", e.getMessage());
        }

        // Fallback: Use authentication object if available
        if (authentication != null) {
            String username = authentication.getName();
            logger.debug("Using username as vertical: {}", username);
            return username;
        }

        logger.warn("Could not extract tenant ID, using default vertical");
        return "default_vertical";
    }

    /**
     * Extract JWT token from request (cookie or header)
     *
     * @param request HTTP request
     * @return JWT token or null if not found
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        // First check in cookies
        final String JWT_COOKIE_NAME = "jwt";
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (JWT_COOKIE_NAME.equals(cookie.getName())) {
                    String tokenValue = cookie.getValue();
                    if (tokenValue != null && !tokenValue.isEmpty()) {
                        return tokenValue;
                    }
                }
            }
        }

        // If not in cookies, fall back to Authorization header
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    /**
     * Extract claims from JWT token without validation
     * This is useful for getting information from token without checking signature
     *
     * @param token JWT token string
     * @return Map of claims or empty map if parsing fails
     */
    private Map<String, Object> extractClaims(String token) {
        try {
            // Split the token
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return Collections.emptyMap();
            }

            // Decode the payload (second part)
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);

            // Parse JSON
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            logger.error("Failed to decode JWT token", e);
            return Collections.emptyMap();
        }
    }

/**
 * Converts Date to LocalDateTime
 */
private LocalDateTime toLocalDateTime(Date date) {
    return date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
}

/**
 * Update the category of a test case
 *
 * @param testCaseId The ID of the test case to update
 * @param newCategory The new category to assign to the test case
 * @return A message indicating success or failure
 */
@Transactional
public String updateTestCaseCategory(String testCaseId, String newCategory) {
    logger.info("Service: Updating category for test case ID: {} to {}", testCaseId, newCategory);

    // Validate category
    try {
        TestCaseCategory.valueOf(newCategory.toUpperCase());
    } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Invalid category: " + newCategory);
    }

    // Find the test case
    UUID testCaseUuid;
    try {
        testCaseUuid = UUID.fromString(testCaseId);
    } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Invalid test case ID format");
    }

    // Update the category
    InteractionTable testCase = interactionTableRepository.findByTestcaseId(testCaseUuid)
            .orElseThrow(() -> new IllegalArgumentException("Test case not found: " + testCaseId));

    testCase.setCategory(newCategory.toUpperCase());
    interactionTableRepository.save(testCase);

    return "Test case category updated successfully";
}

/**
 * Get events for a specific test case
 *
 * @param testCaseId The ID of the test case
 * @return The test case with its events
 */
public TestCaseWithEventsDto getTestCaseEvents(String testCaseId) {
    logger.info("Service: Retrieving events for test case ID: {}", testCaseId);

    // Parse the test case ID
    UUID testCaseUuid;
    try {
        testCaseUuid = UUID.fromString(testCaseId);
    } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Invalid test case ID format");
    }
    
    // Use AtomicBoolean to track if any events were auto-healed (can be modified in lambda)
    final AtomicBoolean hasAutoHealedEvents = new AtomicBoolean(false);

    // Find the test case
    InteractionTable testCase = interactionTableRepository.findByTestcaseId(testCaseUuid)
            .orElseThrow(() -> new IllegalArgumentException("Test case not found: " + testCaseId));

    // Fetch events for this test case
    List<EventsTable> events = eventsTableRepository.findByTestcaseId(testCaseUuid);
    logger.info("Found {} events for test case ID: {}", events.size(), testCaseId);

    // Convert EventsTable entities to EventDto objects and check for auto-healed events
    List<EventDto> eventDtos = events.stream()
            .map(event -> {
                EventDto dto = new EventDto();
                dto.setEventId(event.getId());
                dto.setAbsoluteXPath(event.getAbsolutePath());
                dto.setRelativeXPath(event.getRelativeXpath());
                dto.setRelationalXPath(event.getRelationalXpath());
                dto.setAction(event.getAction());
                dto.setType(event.getType());
                dto.setValue(event.getValue());
                dto.setAssertion(event.getAssertion());
                dto.setAssertionStatus(event.getAssertionStatus());
                
                // Check if this event was auto-healed
                Boolean isAutoHealed = event.getAutoHealed();
                if (isAutoHealed != null && isAutoHealed) {
                    hasAutoHealedEvents.set(true);
                }
                
                dto.setAutohealed(isAutoHealed);
                return dto;
            })
            .collect(Collectors.toList());

    // Create and return the DTO with test case info, events, and autoHealed flag
    return new TestCaseWithEventsDto(
            testCase.getTestcaseId().toString(),
            testCase.getTcId(),
            testCase.getUrl(),
            testCase.getDescription(),
            testCase.getCategory().toLowerCase(),
            hasAutoHealedEvents.get(), // Include the autoHealed flag
            eventDtos);
}

/**
 * Run multiple test cases
 *
 * @param testCaseIds List of test case IDs to run
 * @return Results of the test execution
 */
public List<TestExecutionResultDto> runMultipleTestCases(List<String> testCaseIds, SeleniumTestExecutor seleniumTestExecutor) {
    logger.info("Service: Running multiple test cases: {}", testCaseIds);
    
    CodelessAutomationMethods methods = new CodelessAutomationMethods(
            interactionTableRepository,
            eventsTableRepository,
            featureRepository
    );
    
    return methods.runMultipleTestCases(testCaseIds, seleniumTestExecutor);
}

/**
 * Update events for a test case
 *
 * @param testCaseId The ID of the test case to update
 * @param events The updated list of events
 * @return A message indicating success
 */
public String updateEvents(String testCaseId, List<EventDto> events) {
    logger.info("Service: Updating events for test case ID: {}", testCaseId);
    
    // Parse the test case ID
    UUID testCaseUuid;
    try {
        testCaseUuid = UUID.fromString(testCaseId);
    } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Invalid test case ID format");
    }
    
    // Find the test case
    InteractionTable testCase = interactionTableRepository.findByTestcaseId(testCaseUuid)
            .orElseThrow(() -> new IllegalArgumentException("Test case not found: " + testCaseId));
    
    // Delete existing events for this test case
    eventsTableRepository.deleteByTestcaseId(testCaseUuid);
    
    // Save the updated events
    saveEvents(events, testCase, true);
    
    // Update the last modified timestamp
    testCase.setUpdatedAt(LocalDateTime.now());
    interactionTableRepository.save(testCase);
    
    return "Test case events updated successfully";
    }
}