package com.sdet.sdet360.tenant.service;

import com.sdet.sdet360.tenant.model.Vertical;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class LocatorGeneratorService {
    private static final Logger logger = LoggerFactory.getLogger(LocatorGeneratorService.class);

    public Map<String, String> extractLocatorsFromHtml(String html, String tool) {
        Document doc = Jsoup.parse(html);
        return extractLocators(doc, tool);
    }

    public Map<String, String> extractLocatorsFromHtml(Vertical vertical, String html, String tool) {
        return extractLocatorsFromHtml(html, tool);
    }

    public Map<String, String> extractLocatorsFromUrl(String url, String tool) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Java) Canvas")
                    .get();
            return extractLocators(doc, tool);
        } catch (IOException e) {
            logger.error("Error fetching URL {}: {}", url, e.getMessage());
            throw new RuntimeException("Error fetching URL: " + e.getMessage(), e);
        }
    }

    public Map<String, String> extractLocatorsFromUrl(Vertical vertical, String url, String tool) {
        return extractLocatorsFromUrl(url, tool);
    }

    private Map<String, String> extractLocators(Document doc, String tool) {
        Map<String, String> locators = new LinkedHashMap<>();
        Elements elements = doc.getAllElements();

        for (Element element : elements) {
            if (!isValidElement(element)) continue;

            String keyName = generateVariableName(element);
            String locator = generateLocator(element, tool);

            if (keyName != null && locator != null) {
                locators.put(keyName, locator);
            }
        }

        return locators;
    }

    private boolean isValidElement(Element element) {
        Set<String> ignoredTags = Set.of("script", "noscript", "style", "meta", "link");
        if (ignoredTags.contains(element.tagName())) return false;
        if ("hidden".equalsIgnoreCase(element.attr("type"))) return false;

        if (element.tagName().matches("div|span") &&
                !(element.hasAttr("id") || element.hasAttr("name") || element.hasAttr("placeholder") ||
                        element.hasAttr("aria-label") || element.hasAttr("data-automation-id"))) {
            return false;
        }

        return true;
    }

    private String generateVariableName(Element element) {
        List<String> candidates = Arrays.asList(
                element.attr("data-automation-id"),
                element.attr("name"),
                element.attr("id"),
                element.attr("placeholder"),
                element.attr("aria-label"),
                element.attr("title"),
                String.join("_", element.classNames().stream().limit(2).collect(Collectors.toList()))
        );

        for (String val : candidates) {
            if (val != null && !val.isBlank()) {
                String cleaned = val.replaceAll("[^a-zA-Z0-9]", " ").trim().toLowerCase();
                String[] parts = cleaned.split("\\s+");
                if (parts.length == 0) continue;
                return parts[0] + Arrays.stream(parts, 1, parts.length)
                        .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                        .collect(Collectors.joining());
            }
        }

        return null;
    }

    private String generateLocator(Element element, String tool) {
        if ("cypress".equalsIgnoreCase(tool)) {
            return generateCypressLocator(element);
        }

        // XPath fallback for other tools
        String xpath = null;
        if (element.hasAttr("id")) {
            xpath = String.format("//%s[@id=\"%s\"]", element.tagName(), element.attr("id"));
        } else if (element.hasAttr("name")) {
            xpath = String.format("//%s[@name=\"%s\"]", element.tagName(), element.attr("name"));
        } else if (element.hasAttr("placeholder")) {
            xpath = String.format("//%s[@placeholder=\"%s\"]", element.tagName(), element.attr("placeholder"));
        } else if (element.hasAttr("aria-label")) {
            xpath = String.format("//%s[@aria-label=\"%s\"]", element.tagName(), element.attr("aria-label"));
        } else if (!element.classNames().isEmpty()) {
            String cls = element.classNames().iterator().next();
            xpath = String.format("//%s[contains(@class, \"%s\")]", element.tagName(), cls);
        } else if (element.hasAttr("title")) {
            xpath = String.format("//%s[@title=\"%s\"]", element.tagName(), element.attr("title"));
        } else {
            for (org.jsoup.nodes.Attribute attr : element.attributes()) {
                if (attr.getKey().startsWith("data-")) {
                    xpath = String.format("//%s[@%s=\"%s\"]", element.tagName(), attr.getKey(), attr.getValue());
                    break;
                }
            }
        }

        return xpath != null ? formatLocator(xpath, tool) : null;
    }

    private String generateCypressLocator(Element element) {
        if (element.hasAttr("data-automation-id")) {
            String val = element.attr("data-automation-id");
            if (!val.isBlank() && !isDynamic(val)) {
                return String.format("cy.get(\"[data-automation-id='%s']\")", val);
            }
        }
        return null;
    }

    private boolean isDynamic(String val) {
        return Pattern.compile("^(mui|auto|random|[a-z]+)?[-_]?\\d{2,}$", Pattern.CASE_INSENSITIVE).matcher(val).find();
    }

    private String formatLocator(String xpath, String tool) {
        return switch (tool.toLowerCase()) {
            case "selenium-java" -> String.format("By.xpath(\"%s\");", xpath);
            case "selenium-python" -> String.format("driver.find_element(By.XPATH, \"%s\")", xpath);
            case "robotframework" -> String.format("xpath=%s", xpath);
            case "cypress" -> String.format("cy.get(\"%s\")", xpath);
            default -> throw new IllegalArgumentException("Unsupported tool: " + tool);
        };
    }
}
