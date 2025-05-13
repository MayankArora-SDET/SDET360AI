package com.sdet.sdet360.tenant.service;

import com.sdet.sdet360.tenant.dto.EventDto;
import com.sdet.sdet360.tenant.dto.TestCaseWithEventsDto;
import com.sdet.sdet360.tenant.dto.TestExecutionResultDto;
import com.sdet.sdet360.tenant.enums.TestCaseCategory;
import com.sdet.sdet360.tenant.model.EventsTable;
import com.sdet.sdet360.tenant.model.InteractionTable;
import com.sdet.sdet360.tenant.repository.EventsTableRepository;
import com.sdet.sdet360.tenant.repository.FeatureRepository;
import com.sdet.sdet360.tenant.repository.InteractionTableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class contains the implementation of the methods that need to be added to the CodelessAutomation service.
 * Copy these methods into the CodelessAutomation class.
 */
public class CodelessAutomationMethods {
    
    private static final Logger logger = LoggerFactory.getLogger(CodelessAutomationMethods.class);
    
    private final InteractionTableRepository interactionTableRepository;
    private final EventsTableRepository eventsTableRepository;
    private final FeatureRepository featureRepository;

    public CodelessAutomationMethods(
            InteractionTableRepository interactionTableRepository,
            EventsTableRepository eventsTableRepository,
            FeatureRepository featureRepository) {
        this.interactionTableRepository = interactionTableRepository;
        this.eventsTableRepository = eventsTableRepository;
        this.featureRepository = featureRepository;
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
        
        // Find the test case
        InteractionTable testCase = interactionTableRepository.findByTestcaseId(testCaseUuid)
                .orElseThrow(() -> new IllegalArgumentException("Test case not found: " + testCaseId));
        
        // Fetch events for this test case
        List<EventsTable> events = eventsTableRepository.findByTestcaseId(testCaseUuid);
        logger.info("Found {} events for test case ID: {}", events.size(), testCaseId);
        
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
                testCase.getUrl(),
                testCase.getDescription(),
                testCase.getCategory().toLowerCase(),
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
        
        List<TestExecutionResultDto> results = new ArrayList<>();
        
        for (String testCaseId : testCaseIds) {
            try {
                // Parse the test case ID
                UUID testCaseUuid = UUID.fromString(testCaseId);
                
                // Find the test case
                InteractionTable testCase = interactionTableRepository.findByTestcaseId(testCaseUuid)
                        .orElseThrow(() -> new IllegalArgumentException("Test case not found: " + testCaseId));
                
                // Fetch events for this test case
                List<EventsTable> events = eventsTableRepository.findByTestcaseId(testCaseUuid);
                
                if (events.isEmpty()) { 
                    List<String> emptyEventIds = new ArrayList<>();
                    
                    TestExecutionResultDto emptyResult = TestExecutionResultDto.builder()
                            .testCaseId(testCaseId)
                            .status("FAILED")
                            .eventIds(emptyEventIds)  
                            .errorMessage("No events found for test case")
                            .build();
                    results.add(emptyResult);
                    continue;
                }
                
                // Execute the test case using Selenium WebDriver
                TestExecutionResultDto result = seleniumTestExecutor.executeTestCase(
                        testCaseId,
                        testCase.getUrl(),
                        events
                );
                
                results.add(result);
                
            } catch (Exception e) {
                logger.error("Error running test case {}: {}", testCaseId, e.getMessage(), e);
                TestExecutionResultDto errorResult = TestExecutionResultDto.builder()
                        .testCaseId(testCaseId)
                        .status("FAILED")
                        .errorMessage(e.getMessage())
                        .build();
                results.add(errorResult);
            }
        }
        
        return results;
    }
}
