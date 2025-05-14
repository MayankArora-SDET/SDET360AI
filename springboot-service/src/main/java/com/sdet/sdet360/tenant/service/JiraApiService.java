package com.sdet.sdet360.tenant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.Base64;

import com.sdet.sdet360.grpc.generated.AiRequest;
import com.sdet.sdet360.grpc.generated.AiResponse;
import com.sdet.sdet360.grpc.generated.AiServiceGrpc;

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
     * @param issueType Issue type
     * @param templateName Optional template name
     * @return Search results
     */
    public Map<String, Object> searchIssues(String jiraUrl, String username, String apiToken,
                                            String jql, String issueType, String templateName) {
        JsonNode issues = fetchIssuesByJql(jql, issueType, jiraUrl, username, apiToken);
        if (issues == null || !issues.isArray() || issues.size() == 0) {
            logger.error("No issues found for JQL: {}", jql);
            throw new RuntimeException("No issues found for JQL: " + jql);
        }
        List<String> keys = new ArrayList<>();
        StringBuilder descBuilder = new StringBuilder();

        for (JsonNode iss : issues) {
            keys.add(iss.get("key").asText());

            JsonNode descriptionNode = iss.path("description");
            if (descriptionNode.isObject()) {
                descBuilder.append(formatJiraDescription(descriptionNode, iss.get("key").asText(),
                        iss.path("summary").asText("")));
            } else {
                String description = descriptionNode.asText("");
                descBuilder.append(description);
            }
        }

        String templateToUse;
        if (templateName != null && !templateName.isBlank()) {
            templateToUse = templateName;
        } else {
            templateToUse = "TEST_CASE_GENERATOR_FOR_" + issueType.toUpperCase();
        }
        String aiText = generateAiTestCases(templateToUse, issueType, keys, descBuilder.toString(), "localhost", 50051);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("issues", issues);
        result.put("keys", keys);
        result.put("description", descBuilder.toString());
        result.put("templateName", templateToUse);
        result.put("aiResponse", aiText);
        return result;
    }

    /**
     * Search for multiple issues by keys and generate AI responses for each.
     * @param jiraUrl The base URL of the Jira instance
     * @param username Jira username
     * @param apiToken API token for authentication
     * @param issueKeys List of issue keys
     * @param issueType Issue type
     * @param templateName Optional template name
     * @return Map of issueKey to AI response
     */
    public Map<String, Object> searchIssuesByKeys(String jiraUrl, String username, String apiToken,
                                           List<String> issueKeys, String issueType, String templateName) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (String issueKey : issueKeys) {
            try {
                JsonNode issue = fetchIssueByKey(issueKey, jiraUrl, username, apiToken);
                String summary = issue.path("fields").path("summary").asText("");
                JsonNode descriptionNode = issue.path("fields").path("description");
                String description;
                if (descriptionNode.isObject()) {
                    description = formatJiraDescription(descriptionNode, issueKey, summary);
                } else {
                    description = descriptionNode.asText("");
                }
                List<String> keysList = new ArrayList<>();
                keysList.add(issueKey);
                String aiText = generateAiTestCases(templateName, issueType, keysList, description, "localhost", 50051);
                Map<String, Object> issueResp = new HashMap<>();
                issueResp.put("summary", summary);
                issueResp.put("description", description);
                issueResp.put("aiResponse", aiText);
                result.put(issueKey, issueResp);
            } catch (Exception e) {
                result.put(issueKey, Collections.singletonMap("error", e.getMessage()));
            }
        }
        return result;
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
    public Map<String, Object>makeCustomRequest(String jiraUrl, String username, String apiToken,
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

    /**
     * Fetch a single Jira issue by key
     * @param issueKey The issue key
     * @param jiraUrl The base URL of the Jira instance
     * @param username Jira username
     * @param apiToken API token for authentication
     * @return Issue data as a JsonNode
     */
    public JsonNode fetchIssueByKey(String issueKey, String jiraUrl, String username, String apiToken) {
        logger.info("Fetching issue with key {} from Jira", issueKey);

        String issueEndpoint = jiraUrl.endsWith("/")
                ? jiraUrl + "rest/api/3/issue/" + issueKey
                : jiraUrl + "/rest/api/3/issue/" + issueKey;

        HttpHeaders headers = createAuthHeaders(username, apiToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    issueEndpoint,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Successfully retrieved issue {} from Jira", issueKey);
                return objectMapper.readTree(response.getBody());
            } else {
                logger.error("Failed to fetch Jira issue {}. Status code: {}", issueKey, response.getStatusCode());
                throw new RuntimeException("Failed to fetch Jira issue. Status code: " + response.getStatusCode());
            }
        } catch (RestClientException | JsonProcessingException e) {
            logger.error("Error fetching Jira issue {}: {}", issueKey, e.getMessage(), e);
            throw new RuntimeException("Error fetching Jira issue: " + e.getMessage(), e);
        }
    }

    /**
     * Search Jira issues by JQL with optional issueType filter
     * @param jql JQL query
     * @param issueType Optional issue type (e.g., Bug, Story, Epic)
     * @param jiraUrl The base URL of the Jira instance
     * @param username Jira username
     * @param apiToken API token for authentication
     * @return Filtered ArrayNode of issues with key, summary, description
     */
    public JsonNode fetchIssuesByJql(String jql, String issueType, String jiraUrl, String username, String apiToken) {
        logger.info("Searching issues with JQL: {} and issueType: {}", jql, issueType);
        if (issueType != null && !issueType.isBlank()) {
            jql = jql + " AND issuetype=\"" + issueType + "\"";
        }
        String searchEndpoint = jiraUrl.endsWith("/")
                ? jiraUrl + "rest/api/3/search"
                : jiraUrl + "/rest/api/3/search";
        HttpHeaders headers = createAuthHeaders(username, apiToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            // build JSON body for POST
            ObjectNode body = objectMapper.createObjectNode();
            body.put("jql", jql);
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(body), headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    searchEndpoint, HttpMethod.POST, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode root = objectMapper.readTree(response.getBody());
                ArrayNode arr = objectMapper.createArrayNode();
                for (JsonNode issue : root.path("issues")) {
                    ObjectNode node = objectMapper.createObjectNode();
                    node.put("key", issue.path("key").asText());
                    node.put("summary", issue.path("fields").path("summary").asText());
                    node.set("description", issue.path("fields").path("description"));
                    arr.add(node);
                }
                return arr;
            } else {
                logger.error("Failed to search Jira issues. Status code: {}", response.getStatusCode());
                throw new RuntimeException("Failed to search Jira issues. Status code: " + response.getStatusCode());
            }
        } catch (RestClientException | JsonProcessingException e) {
            logger.error("Error searching Jira issues: {}", e.getMessage(), e);
            throw new RuntimeException("Error searching Jira issues: " + e.getMessage(), e);
        }
    }

    /**
     * Helper to call AI microservice via gRPC
     */
    private String generateAiTestCases(String templateName, String issueType,
                                       List<String> keys, String description,
                                       String host, int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext().build();
        AiServiceGrpc.AiServiceBlockingStub stub = AiServiceGrpc.newBlockingStub(channel);
        AiRequest req = AiRequest.newBuilder()
                .putParameters("template_name", templateName)
                .putParameters("issue_type", issueType)
                .putParameters("keys", String.join(",", keys))
                .putParameters("description", description)
                .build();
        AiResponse resp = stub.generateResponse(req);
        channel.shutdown();
        return resp.getResponseText();
    }

    /**
     * Format JIRA description from Atlassian Document Format (ADF) to plain text
     * @param descriptionNode The description node in ADF format
     * @param issueKey The issue key
     * @param summary The issue summary
     * @return Formatted description text
     */
    private String formatJiraDescription(JsonNode descriptionNode, String issueKey, String summary) {
        StringBuilder formattedDesc = new StringBuilder();

        // Add issue key and summary as header
        formattedDesc.append("# ").append(issueKey).append(": ").append(summary).append("\n\n");

        // Check if it's in the expected format
        if (descriptionNode.has("content")) {
            JsonNode content = descriptionNode.get("content");
            if (content.isArray()) {
                for (JsonNode contentItem : content) {
                    processContentNode(contentItem, formattedDesc, 0);
                }
            }
        }

        formattedDesc.append("\n\n");
        return formattedDesc.toString();
    }

    /**
     * Process a content node from the Atlassian Document Format
     * @param node The content node
     * @param builder The string builder to append to
     * @param depth Current depth for nested elements
     */
    private void processContentNode(JsonNode node, StringBuilder builder, int depth) {
        if (!node.has("type")) {
            return;
        }

        String nodeType = node.get("type").asText();

        switch (nodeType) {
            case "text":
                String text = node.has("text") ? node.get("text").asText() : "";
                // Apply text formatting if marks are present
                if (node.has("marks") && node.get("marks").isArray()) {
                    for (JsonNode mark : node.get("marks")) {
                        String markType = mark.get("type").asText();
                        if ("strong".equals(markType)) {
                            text = "**" + text + "**";
                        } else if ("em".equals(markType)) {
                            text = "*" + text + "*";
                        }
                    }
                }
                builder.append(text);
                break;

            case "paragraph":
                if (depth > 0) {
                    builder.append("\n");
                    for (int i = 0; i < depth; i++) {
                        builder.append("  ");
                    }
                }
                if (node.has("content")) {
                    for (JsonNode content : node.get("content")) {
                        processContentNode(content, builder, depth);
                    }
                }
                builder.append("\n");
                break;

            case "heading":
                int level = node.has("attrs") && node.get("attrs").has("level") ?
                        node.get("attrs").get("level").asInt() : 1;
                builder.append("\n");
                for (int i = 0; i < level; i++) {
                    builder.append("#");
                }
                builder.append(" ");
                if (node.has("content")) {
                    for (JsonNode content : node.get("content")) {
                        processContentNode(content, builder, depth);
                    }
                }
                builder.append("\n");
                break;

            case "bulletList":
                builder.append("\n");
                if (node.has("content")) {
                    for (JsonNode item : node.get("content")) {
                        processContentNode(item, builder, depth + 1);
                    }
                }
                break;

            case "orderedList":
                builder.append("\n");
                if (node.has("content")) {
                    int i = node.has("attrs") && node.get("attrs").has("order") ?
                            node.get("attrs").get("order").asInt() : 1;
                    for (JsonNode item : node.get("content")) {
                        processListItem(item, builder, depth + 1, i++, true);
                    }
                }
                break;

            case "listItem":
                processListItem(node, builder, depth, 1, false);
                break;

            default:
                if (node.has("content")) {
                    for (JsonNode content : node.get("content")) {
                        processContentNode(content, builder, depth);
                    }
                }
                break;
        }
    }

    /**
     * Process a list item node
     * @param node The list item node
     * @param builder The string builder to append to
     * @param depth Current depth for nested elements
     * @param index Item index for ordered lists
     * @param isOrdered Whether this is an ordered list
     */
    private void processListItem(JsonNode node, StringBuilder builder, int depth, int index, boolean isOrdered) {
        builder.append("\n");
        for (int i = 0; i < depth - 1; i++) {
            builder.append("  ");
        }

        if (isOrdered) {
            builder.append(index).append(". ");
        } else {
            builder.append("- ");
        }

        if (node.has("content")) {
            for (JsonNode content : node.get("content")) {
                processContentNode(content, builder, depth);
            }
        }
    }

    // Helper method to create authentication headers
    private HttpHeaders createAuthHeaders(String username, String apiToken) {
        HttpHeaders headers = new HttpHeaders();
        String auth = username + ":" + apiToken;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
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
