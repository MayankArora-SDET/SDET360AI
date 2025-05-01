package com.sdet.sdet360.tenant.controller;

import com.sdet.sdet360.tenant.dto.DomLocatorRequest;
import com.sdet.sdet360.tenant.service.LocatorGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for generating DOM locators either from raw HTML or from a URL.
 */
@RestController
@RequestMapping("/api/locators")
public class LocatorController {

    @Autowired
    private LocatorGeneratorService locatorGeneratorService;

    /**
     * Generate locators from HTML payload.
     * @param request contains HTML and tool type
     * @return map of variable name to locator string
     */
    @PostMapping("/generate_locators")
    public ResponseEntity<Map<String, String>> generateLocatorsFromDom(
            @RequestBody DomLocatorRequest request) {
        Map<String, String> locators = locatorGeneratorService
                .extractLocatorsFromHtml(request.getHtml(), request.getTool());
        return ResponseEntity.ok(locators);
    }

    /**
     * Generate locators from raw HTML in request body (no JSON). Use query param for tool.
     */
    @PostMapping(value = "/generate_locators/raw", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Map<String, String>> generateLocatorsFromRawHtml(
            @RequestParam String tool,
            @RequestBody String html) {
        Map<String, String> locators = locatorGeneratorService.extractLocatorsFromHtml(html, tool);
        return ResponseEntity.ok(locators);
    }

    /**
     * Generate locators by fetching and parsing a URL.
     * @param url the target page URL
     * @param tool locator format: selenium-java|selenium-python|robotframework
     * @return map of variable name to locator string
     */
    @GetMapping("/generate_url_locators")
    public ResponseEntity<Map<String, String>> generateLocatorsFromUrl(
            @RequestParam String url,
            @RequestParam(required = false, defaultValue = "selenium-java") String tool) {
        Map<String, String> locators = locatorGeneratorService
                .extractLocatorsFromUrl(url, tool);
        return ResponseEntity.ok(locators);
    }
}
