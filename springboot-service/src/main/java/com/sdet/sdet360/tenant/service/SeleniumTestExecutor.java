package com.sdet.sdet360.tenant.service;

import com.sdet.sdet360.tenant.dto.TestExecutionResultDto;
import com.sdet.sdet360.tenant.model.EventsTable;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Handles Selenium WebDriver test execution for automated test cases
 */
@Component
public class SeleniumTestExecutor {

    private static final Logger logger = LoggerFactory.getLogger(SeleniumTestExecutor.class);
    private static final String SCREENSHOTS_BASE_DIR = System.getProperty("user.home") + "/sdet360/screenshots";

    /**
     * Executes a test case using Selenium WebDriver
     *
     * @param testCaseId The ID of the test case
     * @param url The URL to navigate to
     * @param events The list of events to execute
     * @return The test execution result
     */
    public TestExecutionResultDto executeTestCase(String testCaseId, String url, List<EventsTable> events) {
        logger.info("Executing test case {} with {} events at URL: {}", testCaseId, events.size(), url);
        
        TestExecutionResultDto result = TestExecutionResultDto.builder()
                .testCaseId(testCaseId)
                .build();
        
        WebDriver driver = null;
        boolean autoHealed = false;
        List<String> screenshots = new ArrayList<>();
        List<Map<String, Object>> executedEvents = new ArrayList<>();
        
        try {
            WebDriverManager.chromedriver().browserVersion("77.0.3865.40").setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("start-maximized");
            options.addArguments("enable-automation");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-infobars");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-browser-side-navigation");
            options.addArguments("--disable-gpu");

            driver = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
            // Create screenshots directory
            Path screenshotsDir = Paths.get(SCREENSHOTS_BASE_DIR, testCaseId);
            Files.createDirectories(screenshotsDir);
            
            // Navigate to URL
            driver.get(url);
            
            // Take initial screenshot
            String initialScreenshot = takeScreenshot(driver, screenshotsDir, "initial");
            screenshots.add(initialScreenshot);
            
            // Execute each event
            int eventIndex = 0;
            for (EventsTable event : events) {
                Map<String, Object> executedEvent = new HashMap<>();
                executedEvent.put("action", event.getAction());
                executedEvent.put("relativeXPath", event.getRelativeXpath());
                executedEvent.put("value", event.getValue());
                
                try {
                    String action = event.getAction();
                    String xpath = event.getRelativeXpath();
                    String value = event.getValue();
                    
                    if (xpath == null || xpath.isEmpty()) {
                        logger.warn("Skipping event with empty XPath: {}", event);
                        executedEvent.put("executed", false);
                        executedEvent.put("error", "Empty XPath");
                        continue;
                    }
                    
                    // Wait for element to be present and visible
                    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
                    
                    // Execute action based on type
                    if ("click".equalsIgnoreCase(action)) {
                        element.click();
                        executedEvent.put("executed", true);
                    } else if (value != null && !value.isEmpty()) {
                        // Input action
                        element.clear();
                        element.sendKeys(value);
                        executedEvent.put("executed", true);
                    } else {
                        logger.warn("Unknown action or missing value: {}", event);
                        executedEvent.put("executed", false);
                        executedEvent.put("error", "Unknown action or missing value");
                    }
                    
                    // Take screenshot after action
                    String screenshotPath = takeScreenshot(driver, screenshotsDir, "event_" + eventIndex);
                    screenshots.add(screenshotPath);
                    
                } catch (Exception e) {
                    logger.error("Error executing event {}: {}", event, e.getMessage());
                    executedEvent.put("executed", false);
                    executedEvent.put("error", e.getMessage());
                    
                    // Try self-healing here (simplified version)
                    try {
                        // Try a more flexible XPath or alternative locator strategy
                        String relaxedXPath = relaxXPath(event.getRelativeXpath());
                        WebElement healedElement = driver.findElement(By.xpath(relaxedXPath));
                        
                        if (healedElement != null) {
                            autoHealed = true;
                            executedEvent.put("autoHealed", true);
                            executedEvent.put("healedXPath", relaxedXPath);
                            
                            if ("click".equalsIgnoreCase(event.getAction())) {
                                healedElement.click();
                                executedEvent.put("executed", true);
                            } else if (event.getValue() != null && !event.getValue().isEmpty()) {
                                healedElement.clear();
                                healedElement.sendKeys(event.getValue());
                                executedEvent.put("executed", true);
                            }
                            
                            // Take screenshot after auto-healing
                            String screenshotPath = takeScreenshot(driver, screenshotsDir, "healed_" + eventIndex);
                            screenshots.add(screenshotPath);
                        }
                    } catch (Exception healingException) {
                        logger.error("Auto-healing failed: {}", healingException.getMessage());
                        executedEvent.put("autoHealingAttempted", true);
                        executedEvent.put("autoHealingFailed", true);
                    }
                }
                
                executedEvents.add(executedEvent);
                eventIndex++;
            }
            
            // Take final screenshot
            String finalScreenshot = takeScreenshot(driver, screenshotsDir, "final");
            screenshots.add(finalScreenshot);
            
            // Set result
            result.setStatus("SUCCESS");
            result.setAutoHealed(autoHealed);
            result.setScreenshots(screenshots);
            result.setExecutedEvents(executedEvents);
            
        } catch (Exception e) {
            logger.error("Error executing test case {}: {}", testCaseId, e.getMessage(), e);
            result.setStatus("FAILED");
            result.setErrorMessage(e.getMessage());
            result.setScreenshots(screenshots);
            result.setExecutedEvents(executedEvents);
        } finally {
            // Clean up WebDriver
            if (driver != null) {
                try {
                    driver.quit();
                } catch (Exception e) {
                    logger.error("Error closing WebDriver: {}", e.getMessage());
                }
            }
        }
        
        return result;
    }
    
    /**
     * Takes a screenshot and saves it to the specified directory
     *
     * @param driver The WebDriver instance
     * @param screenshotsDir The directory to save screenshots to
     * @param name The name of the screenshot
     * @return The path to the screenshot
     */
    private String takeScreenshot(WebDriver driver, Path screenshotsDir, String name) throws Exception {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String filename = name + "_" + UUID.randomUUID() + ".png";
        Path destination = screenshotsDir.resolve(filename);
        Files.copy(screenshot.toPath(), destination);
        return destination.toString();
    }
    
    /**
     * Relaxes an XPath to make it more flexible for self-healing
     *
     * @param xpath The original XPath
     * @return A more flexible XPath
     */
    private String relaxXPath(String xpath) {
        // This is a simplified example of XPath relaxation
        // In a real implementation, you would use more sophisticated techniques
        
        // Remove position predicates
        String relaxed = xpath.replaceAll("\\[\\d+\\]", "");
        
        // Make contains() for text and attributes
        if (relaxed.contains("@id='")) {
            relaxed = relaxed.replaceAll("@id='([^']*)'", "contains(@id,'$1')");
        }
        
        if (relaxed.contains("@class='")) {
            relaxed = relaxed.replaceAll("@class='([^']*)'", "contains(@class,'$1')");
        }
        
        if (relaxed.contains("text()='")) {
            relaxed = relaxed.replaceAll("text\\(\\)='([^']*)'", "contains(text(),'$1')");
        }
        
        return relaxed;
    }
}
