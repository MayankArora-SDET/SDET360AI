package com.sdet.sdet360.tenant.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for self-healing Selenium tests by automatically fixing broken locators
 */
@Service
public class SelfHealingService {

    private static final Logger logger = LoggerFactory.getLogger(SelfHealingService.class);
    private WebDriver driver;
    private WebDriverWait wait;
    private String bestMatchXpath;

    /**
     * Initializes the SelfHealingService with a WebDriver
     *
     * @param driver The WebDriver instance
     */
    public void initialize(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Gets a clickable element using the provided XPath
     * If the original XPath fails, attempts to heal it
     *
     * @param relativeXpath The original XPath
     * @return A tuple containing the WebElement and a boolean indicating if auto-healing was used
     */
    public Map<String, Object> getClickableElement(String relativeXpath) {
        Map<String, Object> result = new HashMap<>();
        boolean autoHealed = false;
        WebElement element = null;

        try {
            element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(relativeXpath)));
            result.put("element", element);
            result.put("autoHealed", autoHealed);
            return result;
        } catch (Exception e) {
            logger.warn("Original XPath not clickable: {}. Error: {}", relativeXpath, e.getMessage());
            try {
                element = healXpath(relativeXpath);
                autoHealed = true;
                result.put("element", element);
                result.put("autoHealed", autoHealed);
                result.put("healedXpath", bestMatchXpath);
                return result;
            } catch (Exception healException) {
                logger.error("Failed to heal XPath: {}", healException.getMessage());
                throw healException;
            }
        }
    }

    /**
     * Attempts to heal a broken XPath by finding the best matching element
     *
     * @param brokenXpath The broken XPath
     * @return The healed WebElement
     */
    private WebElement healXpath(String brokenXpath) {
        String expectedTag = extractTagFromXpath(brokenXpath);
        Map<String, String> expectedAttributes = extractAttributesFromXpath(brokenXpath);

        if (expectedTag == null || expectedAttributes.isEmpty()) {
            throw new RuntimeException("No valid tag/attributes found in the broken XPath: " + brokenXpath);
        }

        List<WebElement> candidates = driver.findElements(By.tagName(expectedTag));
        if (candidates.isEmpty()) {
            throw new RuntimeException("No matching tag found for healing: " + expectedTag);
        }

        WebElement bestMatch = null;
        double bestMatchScore = Double.MAX_VALUE;

        for (WebElement element : candidates) {
            String elementXpath = generateXpath(element);
            double score = calculateSimilarityScore(expectedAttributes, element);
            logger.debug("Trying candidate: {} with score: {}", elementXpath, score);
            
            if (score < bestMatchScore) {
                bestMatchScore = score;
                bestMatch = element;
                bestMatchXpath = elementXpath;
            }
        }

        if (bestMatch != null) {
            logger.info("Healed XPath found: {}", bestMatchXpath);
            return bestMatch;
        }

        throw new RuntimeException("No healed XPath found for: " + brokenXpath);
    }

    /**
     * Extracts the tag name from an XPath
     *
     * @param xpath The XPath
     * @return The tag name
     */
    private String extractTagFromXpath(String xpath) {
        Pattern pattern = Pattern.compile("//([\\w-]+)");
        Matcher matcher = pattern.matcher(xpath);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    /**
     * Extracts attributes from an XPath
     *
     * @param xpath The XPath
     * @return A map of attribute names to values
     */
    private Map<String, String> extractAttributesFromXpath(String xpath) {
        Map<String, String> attributes = new HashMap<>();
        Pattern pattern = Pattern.compile("@([\\w-]+)=[\"'](.*?)[\"']");
        Matcher matcher = pattern.matcher(xpath);
        
        while (matcher.find()) {
            attributes.put(matcher.group(1), matcher.group(2));
        }
        
        logger.debug("Extracted attributes: {}", attributes);
        return attributes;
    }

    /**
     * Generates an XPath for an element
     *
     * @param element The WebElement
     * @return The generated XPath
     */
    private String generateXpath(WebElement element) {
        String tagName = element.getTagName();
        String[] attributes = {"name", "id", "class", "placeholder", "type"};
        
        for (String attr : attributes) {
            String value = element.getAttribute(attr);
            if (value != null && !value.isEmpty()) {
                return "//" + tagName + "[@" + attr + "='" + value + "']";
            }
        }
        
        return "//" + tagName;
    }

    /**
     * Calculates a similarity score between expected attributes and an element
     *
     * @param expectedAttributes The expected attributes
     * @param element The WebElement
     * @return The similarity score (lower is better)
     */
    private double calculateSimilarityScore(Map<String, String> expectedAttributes, WebElement element) {
        double score = 0;
        for (Map.Entry<String, String> entry : expectedAttributes.entrySet()) {
            String attr = entry.getKey();
            String expectedValue = entry.getValue();
            String actualValue = element.getAttribute(attr);
            
            if (actualValue != null && !actualValue.isEmpty()) {
                score += levenshteinDistance(expectedValue, actualValue);
            } else {
                score += expectedValue.length();
            }
        }
        return score;
    }

    /**
     * Calculates the Levenshtein distance between two strings
     *
     * @param s1 The first string
     * @param s2 The second string
     * @return The Levenshtein distance
     */
    private int levenshteinDistance(String s1, String s2) {
        int diffCount = 0;
        int minLength = Math.min(s1.length(), s2.length());
        
        for (int i = 0; i < minLength; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                diffCount++;
            }
        }
        
        return diffCount + Math.abs(s1.length() - s2.length());
    }

    /**
     * Gets the best match XPath found during healing
     *
     * @return The best match XPath
     */
    public String getBestMatchXpath() {
        return bestMatchXpath;
    }
}
