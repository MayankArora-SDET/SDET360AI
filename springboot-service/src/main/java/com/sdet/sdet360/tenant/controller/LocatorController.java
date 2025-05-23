package com.sdet.sdet360.tenant.controller;

import com.sdet.sdet360.tenant.dto.DomLocatorRequest;
import com.sdet.sdet360.tenant.model.Vertical;
import com.sdet.sdet360.tenant.repository.VerticalRepository;
import com.sdet.sdet360.tenant.service.LocatorGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller for generating DOM locators either from raw HTML or from a URL.
 */
@RestController
@RequestMapping("/api/locators/{verticalId}")
public class LocatorController {

    @Autowired
    private LocatorGeneratorService locatorGeneratorService;

    @Autowired
    private VerticalRepository verticalRepository;

    /**
     * Generate locators from HTML payload.
     * @param verticalId the ID of the vertical
     * @param request contains HTML and tool type
     * @return map of variable name to locator string
     */
    @PostMapping(value = "/generate_locators", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> generateLocatorsFromDom(
            @PathVariable UUID verticalId,
            @RequestBody DomLocatorRequest request) {
        Optional<Vertical> verticalOpt = verticalRepository.findById(verticalId);
        if (!verticalOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Vertical vertical = verticalOpt.get();
        Map<String, String> locators = locatorGeneratorService
                .extractLocatorsFromHtml(vertical, request.getHtml(), request.getTool());
        return ResponseEntity.ok(locators);
    }

    /**
     * Generate locators from raw HTML in request body (no JSON). Use query param for tool.
     */
    @PostMapping(value = "/generate_locators", consumes = {MediaType.TEXT_PLAIN_VALUE, MediaType.TEXT_HTML_VALUE})
    public ResponseEntity<Map<String, String>> generateLocatorsFromRawHtml(
            @PathVariable UUID verticalId,
            @RequestParam(defaultValue = "cypress") String tool,
            @RequestBody String html) {
        Optional<Vertical> verticalOpt = verticalRepository.findById(verticalId);
        if (!verticalOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Vertical vertical = verticalOpt.get();
        Map<String, String> locators = locatorGeneratorService.extractLocatorsFromHtml(vertical, html, tool);
        return ResponseEntity.ok(locators);
    }

    /**
     * Generate locators by fetching and parsing a URL.
     * @param verticalId the ID of the vertical
     * @param url the target page URL
     * @param tool locator format: selenium-java|selenium-python|robotframework
     * @return map of variable name to locator string
     */
    @GetMapping("/generate_url_locators")
    public ResponseEntity<Map<String, String>> generateLocatorsFromUrl(
            @PathVariable UUID verticalId,
            @RequestParam String url,
            @RequestParam(required = false, defaultValue = "cypress") String tool) {
        Optional<Vertical> verticalOpt = verticalRepository.findById(verticalId);
        if (!verticalOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Vertical vertical = verticalOpt.get();
        Map<String, String> locators = locatorGeneratorService
                .extractLocatorsFromUrl(vertical, url, tool);
        return ResponseEntity.ok(locators);
    }
}
