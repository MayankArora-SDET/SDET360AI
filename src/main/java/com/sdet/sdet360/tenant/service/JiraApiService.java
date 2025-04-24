package com.sdet.sdet360.tenant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdet.sdet360.tenant.model.Project;
import com.sdet.sdet360.tenant.model.Vertical;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Base64;

@Service
public class JiraApiService {
    private static final Logger logger = LoggerFactory.getLogger(JiraApiService.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public JiraApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Get all projects from Jira
     * @param jiraUrl The base URL of the Jira instance
     * @param username Jira username
     * @param apiToken API token for authentication
     * @return List of Jira projects
     */
    public List<Map<String, Object>> getAllProjects(String jiraUrl, String username, String apiToken) {
        logger.info("Fetching all projects from Jira at {}", jiraUrl);

        String projectsEndpoint = jiraUrl.endsWith("/")
                ? jiraUrl + "rest/api/3/project"
                : jiraUrl + "/rest/api/3/project";

        HttpHeaders headers = createAuthHeaders(username, apiToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    projectsEndpoint,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Successfully retrieved projects from Jira");
                return parseProjectsResponse(response.getBody());
            } else {
                logger.error("Failed to fetch projects. Status code: {}", response.getStatusCode());
                throw new RuntimeException("Failed to fetch projects from Jira. Status code: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            logger.error("Error connecting to Jira: {}", e.getMessage(), e);
            throw new RuntimeException("Error connecting to Jira: " + e.getMessage(), e);
        }
    }

    /**
     * Get a specific project from Jira by key
     * @param jiraUrl The base URL of the Jira instance
     * @param username Jira username
     * @param apiToken API token for authentication
     * @param projectKey The project key
     * @return Project details
     */
    public Map<String, Object> getProjectByKey(String jiraUrl, String username, String apiToken, String projectKey) {
        logger.info("Fetching project with key {} from Jira", projectKey);

        String projectEndpoint = jiraUrl.endsWith("/")
                ? jiraUrl + "rest/api/3/project/" + projectKey
                : jiraUrl + "/rest/api/3/project/" + projectKey;

        HttpHeaders headers = createAuthHeaders(username, apiToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    projectEndpoint,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Successfully retrieved project {} from Jira", projectKey);
                return objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
            } else {
                logger.error("Failed to fetch project {}. Status code: {}", projectKey, response.getStatusCode());
                throw new RuntimeException("Failed to fetch project from Jira. Status code: " + response.getStatusCode());
            }
        } catch (RestClientException | JsonProcessingException e) {
            logger.error("Error fetching project {}: {}", projectKey, e.getMessage(), e);
            throw new RuntimeException("Error fetching project from Jira: " + e.getMessage(), e);
        }
    }

    /**
     * Create a new issue in Jira
     * @param jiraUrl The base URL of the Jira instance
     * @param username Jira username
     * @param apiToken API token for authentication
     * @param issueData Map containing the issue data
     * @return Created issue data
     */
    public Map<String, Object> createIssue(String jiraUrl, String username, String apiToken, Map<String, Object> issueData) {
        logger.info("Creating a new issue in Jira");

        String issueEndpoint = jiraUrl.endsWith("/")
                ? jiraUrl + "rest/api/3/issue"
                : jiraUrl + "/rest/api/3/issue";

        HttpHeaders headers = createAuthHeaders(username, apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        try {
            String requestBody = objectMapper.writeValueAsString(issueData);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    issueEndpoint,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Successfully created issue in Jira");
                return objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
            } else {
                logger.error("Failed to create issue. Status code: {}", response.getStatusCode());
                throw new RuntimeException("Failed to create issue in Jira. Status code: " + response.getStatusCode());
            }
        } catch (RestClientException | JsonProcessingException e) {
            logger.error("Error creating issue: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating issue in Jira: " + e.getMessage(), e);
        }
    }

    /**
     * Search for issues using JQL
     * @param jiraUrl The base URL of the Jira instance
     * @param username Jira username
     * @param apiToken API token for authentication
     * @param jql JQL query string
     * @param startAt Pagination start
     * @param maxResults Maximum results to return
     * @return Search results
     */
    public Map<String, Object> searchIssues(String jiraUrl, String username, String apiToken,
                                            String jql, int startAt, int maxResults) {
        logger.info("Searching for issues with JQL: {}", jql);

        String searchEndpoint = jiraUrl.endsWith("/")
                ? jiraUrl + "rest/api/3/search"
                : jiraUrl + "/rest/api/3/search";

        HttpHeaders headers = createAuthHeaders(username, apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("jql", jql);
        requestBody.put("startAt", startAt);
        requestBody.put("maxResults", maxResults);

        try {
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    searchEndpoint,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Successfully searched for issues in Jira");
                return objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
            } else {
                logger.error("Failed to search issues. Status code: {}", response.getStatusCode());
                throw new RuntimeException("Failed to search issues in Jira. Status code: " + response.getStatusCode());
            }
        } catch (RestClientException | JsonProcessingException e) {
            logger.error("Error searching issues: {}", e.getMessage(), e);
            throw new RuntimeException("Error searching issues in Jira: " + e.getMessage(), e);
        }
    }

    /**
     * Make custom API request to Jira
     * @param jiraUrl The base URL of the Jira instance
     * @param username Jira username
     * @param apiToken API token for authentication
     * @param endpoint API endpoint (will be appended to base URL)
     * @param method HTTP method
     * @param requestBody Request body (can be null for GET requests)
     * @param queryParams Query parameters
     * @return API response as a map
     */
    public Map<String, Object> makeCustomRequest(String jiraUrl, String username, String apiToken,
                                                 String endpoint, HttpMethod method,
                                                 Object requestBody, Map<String, String> queryParams) {
        logger.info("Making custom {} request to Jira endpoint: {}", method, endpoint);

        String fullEndpoint = jiraUrl.endsWith("/")
                ? jiraUrl + endpoint
                : jiraUrl + "/" + endpoint;

        // Add query parameters if provided
        if (queryParams != null && !queryParams.isEmpty()) {
            StringBuilder queryString = new StringBuilder("?");
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                if (queryString.length() > 1) {
                    queryString.append("&");
                }
                queryString.append(entry.getKey()).append("=").append(entry.getValue());
            }
            fullEndpoint += queryString.toString();
        }

        HttpHeaders headers = createAuthHeaders(username, apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<?> entity;
        if (requestBody != null) {
            try {
                String requestBodyJson = objectMapper.writeValueAsString(requestBody);
                entity = new HttpEntity<>(requestBodyJson, headers);
            } catch (JsonProcessingException e) {
                logger.error("Error serializing request body: {}", e.getMessage(), e);
                throw new RuntimeException("Error serializing request body: " + e.getMessage(), e);
            }
        } else {
            entity = new HttpEntity<>(headers);
        }

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    fullEndpoint,
                    method,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Custom request to Jira successful");
                if (response.getBody() == null || response.getBody().isEmpty()) {
                    return Collections.emptyMap();
                }
                return objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
            } else {
                logger.error("Custom request failed. Status code: {}", response.getStatusCode());
                throw new RuntimeException("Custom request to Jira failed. Status code: " + response.getStatusCode());
            }
        } catch (RestClientException | JsonProcessingException e) {
            logger.error("Error making custom request: {}", e.getMessage(), e);
            throw new RuntimeException("Error making custom request to Jira: " + e.getMessage(), e);
        }
    }

    // Helper method to create authentication headers
    private HttpHeaders createAuthHeaders(String username, String apiToken) {
        HttpHeaders headers = new HttpHeaders();
        String auth = username + ":" + apiToken;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth, StandardCharsets.UTF_8);
        headers.set("Authorization", authHeader);
        return headers;
    }

    // Helper method to parse projects response
    private List<Map<String, Object>> parseProjectsResponse(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, new TypeReference<List<Map<String, Object>>>() {});
        } catch (JsonProcessingException e) {
            logger.error("Error parsing projects response: {}", e.getMessage(), e);
            throw new RuntimeException("Error parsing projects response: " + e.getMessage(), e);
        }
    }
}