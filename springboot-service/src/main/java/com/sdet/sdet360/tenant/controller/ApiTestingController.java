package com.sdet.sdet360.tenant.controller;

import com.sdet.sdet360.tenant.auth.TenantAwareUserDetails;
import com.sdet.sdet360.tenant.model.Vertical;
import com.sdet.sdet360.tenant.repository.VerticalRepository;
import com.sdet.sdet360.tenant.service.ApiTestingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/api-testing")
public class ApiTestingController {
    private static final Logger logger = LoggerFactory.getLogger(ApiTestingController.class);

    @Autowired
    private VerticalRepository verticalRepository;



    @Autowired
    private ApiTestingService apiTestingService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Generate API testing scenarios for a given endpoint
     * 
     * @param verticalId The vertical ID for the tenant
     * @param requestBody API details including URL, method, parameters, headers, and body
     * @return Test scenarios for the API
     */
    @PostMapping("/generate/{verticalId}")
    public ResponseEntity<?> generateApiTestScenarios(
            @PathVariable UUID verticalId,
            @RequestBody Map<String, Object> requestBody) {

        logger.info("User '{}' initiated API test scenario generation for Vertical ID: {}",
                 verticalId);

        Optional<Vertical> verticalOpt = verticalRepository.findById(verticalId);
        if (!verticalOpt.isPresent()) {
            logger.warn("Vertical with ID '{}' not found", verticalId);
            return ResponseEntity.notFound().build();
        }

        Vertical vertical = verticalOpt.get();
        logger.debug("Loaded Vertical: id={}, name={}", vertical.getId(), vertical.getName());

        try {
            String url = (String) requestBody.get("url");
            String method = (String) requestBody.getOrDefault("method", "GET");
            List<Map<String, Object>> params = requestBody.containsKey("params") ?
                    (List<Map<String, Object>>) requestBody.get("params") : Collections.emptyList();
            List<Map<String, Object>> headers = requestBody.containsKey("headers") ?
                    (List<Map<String, Object>>) requestBody.get("headers") : Collections.emptyList();
            Object body = requestBody.get("body");

            Map<String, Object> result = apiTestingService.generateApiTestScenarios(
                    vertical.getId(), url, method, params, headers, body
            );

            logger.info("Successfully generated API test scenarios for URL: {}", url);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            logger.error("Error generating API test scenarios for Vertical ID {}: {}",
                    verticalId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body("Error generating API test scenarios: " + e.getMessage());
        }
    }

    /**
     * Generate API testing scenarios for a Jira API
     * 
     * @param verticalId The vertical ID for the tenant
     * @param endpoint Jira API endpoint
     * @param method HTTP method
     * @param requestBody Request body for the API call
     * @param currentUser The authenticated user
     * @return Test scenarios for the Jira API
     */
    @PostMapping("/jira-api/{verticalId}")
    public ResponseEntity<?> generateJiraApiTestScenarios(
            @PathVariable UUID verticalId,
            @RequestParam String endpoint,
            @RequestParam String method,
            @RequestBody(required = false) Map<String, Object> requestBody,
            @RequestParam(required = false) Map<String, String> queryParams,
            @AuthenticationPrincipal TenantAwareUserDetails currentUser) {
        
        logger.info("User '{}' initiated Jira API test scenario generation for Vertical ID: {}, endpoint: {}", 
                currentUser.getUsername(), verticalId, endpoint);

        Optional<Vertical> verticalOpt = verticalRepository.findById(verticalId);
        if (!verticalOpt.isPresent()) {
            logger.warn("Vertical with ID '{}' not found", verticalId);
            return ResponseEntity.notFound().build();
        }

        Vertical vertical = verticalOpt.get();
        logger.debug("Loaded Vertical: id={}, name={}", vertical.getId(), vertical.getName());

        try {
            // Construct the full Jira API URL
            String jiraUrl = vertical.getJiraServerUrl();
            String fullUrl = jiraUrl.endsWith("/") 
                ? jiraUrl + endpoint 
                : jiraUrl + "/" + endpoint;
            
            // Convert query parameters to params format
            List<Map<String, Object>> params = new ArrayList<>();
            if (queryParams != null) {
                for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("key", entry.getKey());
                    param.put("value", entry.getValue());
                    param.put("description", "Query parameter");
                    params.add(param);
                }
            }
            
            // Add authentication headers
            List<Map<String, Object>> headers = new ArrayList<>();
            Map<String, Object> authHeader = new HashMap<>();
            authHeader.put("key", "Authorization");
            authHeader.put("value", "Basic " + Base64.getEncoder().encodeToString(
                    (vertical.getJiraUsername() + ":" + vertical.getApiKey()).getBytes()));
            authHeader.put("description", "Authentication header");
            headers.add(authHeader);
            
            // Add content type header if there's a request body
            if (requestBody != null && !requestBody.isEmpty()) {
                Map<String, Object> contentTypeHeader = new HashMap<>();
                contentTypeHeader.put("key", "Content-Type");
                contentTypeHeader.put("value", "application/json");
                contentTypeHeader.put("description", "Content type header");
                headers.add(contentTypeHeader);
            }

            // Generate test scenarios using the ApiTestingService
            Map<String, Object> result = apiTestingService.generateApiTestScenarios(
                vertical.getId(),
                fullUrl,
                method,
                params,
                headers,
                requestBody
            );

            logger.info("Successfully generated Jira API test scenarios for endpoint: {}", endpoint);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Error generating Jira API test scenarios for Vertical ID {}: {}", 
                    verticalId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body("Error generating Jira API test scenarios: " + e.getMessage());
        }
    }
}
