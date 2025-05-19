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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LocatorGeneratorService {
    private static final Logger logger = LoggerFactory.getLogger(LocatorGeneratorService.class);

    public Map<String, String> extractLocatorsFromHtml(String html, String tool) {
        Document doc = Jsoup.parse(html);
        return extractLocators(doc, tool);
    }

    public Map<String, String> extractLocatorsFromHtml(Vertical vertical, String html, String tool) {
        // Use vertical context if needed
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
        // Use vertical context if needed
        return extractLocatorsFromUrl(url, tool);
    }

    private Map<String, String> extractLocators(Document doc, String tool) {
        Map<String, String> locators = new LinkedHashMap<>();
        Elements elements = doc.getAllElements();
        for (Element element : elements) {
            if (!isValidElement(element)) continue;
            String keyName = generateVariableName(element);
            String locator = generateLocator(element, tool);
            if (keyName != null && !keyName.isEmpty() && locator != null) {
                locators.put(keyName, locator);
            }
        }
        return locators;
    }

    private boolean isValidElement(Element element) {
        String tag = element.tagName();
        Set<String> ignored = Set.of("script", "noscript", "style", "meta", "link");
        if (ignored.contains(tag)) return false;
        if ("hidden".equalsIgnoreCase(element.attr("type"))) return false;
        if ((tag.equals("div") || tag.equals("span"))
                && !element.hasAttr("id")
                && !element.hasAttr("name")
                && !element.hasAttr("aria-label")
                && !element.hasAttr("placeholder")
                && element.attributes().asList().stream().noneMatch(a -> a.getKey().startsWith("data-"))) {
            return false;
        }
        return true;
    }

    private String generateVariableName(Element element) {
        List<String> sources = List.of(
                element.attr("id"),
                element.attr("name"),
                element.attr("placeholder"),
                element.attr("aria-label"),
                element.attr("title")
        );
        String cls = element.classNames().stream().limit(2).collect(Collectors.joining("_"));
        if (!cls.isBlank()) sources = List.<String>copyOf(sources).stream().collect(Collectors.toList());
        // data- attribute
        String dataAttr = element.attributes().asList().stream()
                .map(a -> a.getKey())
                .filter(k -> k.startsWith("data-"))
                .findFirst().orElse(null);
        if (dataAttr != null) sources = List.<String>copyOf(sources).stream().collect(Collectors.toList());
        for (String value : sources) {
            if (value != null && !value.isBlank()) {
                return value.replaceAll("[^A-Za-z0-9_]", "_").toLowerCase();
            }
        }
        return null;
    }

    private String generateLocator(Element element, String tool) {
        String locator = null;

        if ("cypress".equalsIgnoreCase(tool)) {
            for (org.jsoup.nodes.Attribute attr : element.attributes()) {
                if (attr.getKey().equalsIgnoreCase("data-automation-id") && !attr.getValue().isBlank()) {
                    locator = String.format("[data-automation-id='%s']", attr.getValue());
                    break;
                }
            }
            if (locator == null && element.hasAttr("name")) {
                locator = String.format("%s[name='%s']", element.tagName(), element.attr("name"));
            } else if (locator == null && element.hasAttr("placeholder")) {
                locator = String.format("%s[placeholder='%s']", element.tagName(), element.attr("placeholder"));
            } else if (locator == null && element.hasAttr("aria-label")) {
                locator = String.format("%s[aria-label='%s']", element.tagName(), element.attr("aria-label"));
            } else if (locator == null && element.hasAttr("title")) {
                locator = String.format("%s[title='%s']", element.tagName(), element.attr("title"));
            } else if (locator == null && element.hasAttr("id")) {
                locator = String.format("%s#%s", element.tagName(), element.attr("id"));
            } else if (locator == null && !element.classNames().isEmpty()) {
                locator = String.format("%s.%s", element.tagName(), element.classNames().iterator().next());
            }

            if (locator == null) return null;
            return formatLocator(locator, tool);
        }

        // Default XPath-based locators
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
            String firstClass = element.classNames().iterator().next();
            xpath = String.format("//%s[contains(@class, \"%s\")]", element.tagName(), firstClass);
        } else if (element.hasAttr("title")) {
            xpath = String.format("//%s[@title=\"%s\"]", element.tagName(), element.attr("title"));
        } else {
            String dataKey = element.attributes().asList().stream()
                    .map(a -> a.getKey())
                    .filter(k -> k.startsWith("data-"))
                    .findFirst().orElse(null);
            if (dataKey != null) {
                xpath = String.format("//%s[@%s=\"%s\"]", element.tagName(), dataKey, element.attr(dataKey));
            }
        }
        if (xpath == null) return null;
        return formatLocator(xpath, tool);
    }

    private String formatLocator(String xpath, String tool) {
        switch (tool.toLowerCase()) {
            case "selenium-java":
                return String.format("By.xpath(\"%s\");", xpath);
            case "selenium-python":
                return String.format("driver.find_element(By.XPATH, \"%s\")", xpath);
            case "robotframework":
                return String.format("xpath=%s", xpath);
            case "cypress":
                return String.format("cy.get(\"%s\")", xpath);
            default:
                throw new IllegalArgumentException("Unsupported tool: " + tool);
        }
    }
}
