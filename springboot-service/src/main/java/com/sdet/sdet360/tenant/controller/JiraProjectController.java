package com.sdet.sdet360.tenant.controller;

import com.sdet.sdet360.tenant.auth.TenantAwareUserDetails;
import com.sdet.sdet360.tenant.model.Project;
import com.sdet.sdet360.tenant.model.Vertical;
import com.sdet.sdet360.tenant.repository.ProjectRepository;
import com.sdet.sdet360.tenant.repository.VerticalRepository;
import com.sdet.sdet360.tenant.service.JiraApiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.sdet.sdet360.grpc.generated.AiServiceGrpc;
import com.sdet.sdet360.grpc.generated.AiRequest;
import com.sdet.sdet360.grpc.generated.AiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jira-projects")
public class JiraProjectController {
    private static final Logger logger = LoggerFactory.getLogger(JiraProjectController.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private VerticalRepository verticalRepository;

    @Autowired
    private JiraApiService jiraApiService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/fetch/{verticalId}")
    public ResponseEntity<?> fetchAndSaveJiraProjects(@PathVariable UUID verticalId,
                                                      @AuthenticationPrincipal TenantAwareUserDetails currentUser) {
        logger.info("User '{}' initiated fetch for Vertical ID: {}", currentUser.getUsername(), verticalId);

        Optional<Vertical> verticalOpt = verticalRepository.findById(verticalId);
        if (!verticalOpt.isPresent()) {
            logger.warn("Vertical with ID '{}' not found", verticalId);
            return ResponseEntity.notFound().build();
        }

        Vertical vertical = verticalOpt.get();
        logger.debug("Loaded Vertical: id={}, name={}, jiraUrl={}", vertical.getId(), vertical.getName(), vertical.getJiraServerUrl());

        List<Project> savedProjects = new ArrayList<>();
        try {
            logger.info("Connecting to JIRA at '{}' with username '{}'", vertical.getJiraServerUrl(), vertical.getJiraUsername());

            // Use JiraApiService to get all projects
            List<Map<String, Object>> jiraProjects = jiraApiService.getAllProjects(
                    vertical.getJiraServerUrl(),
                    vertical.getJiraUsername(),
                    vertical.getApiKey()
            );

            logger.info("Retrieved {} projects from JIRA", jiraProjects.size());

            for (Map<String, Object> jiraProject : jiraProjects) {
                String projectKey = (String) jiraProject.get("key");
                String projectName = (String) jiraProject.get("name");

                logger.debug("Processing JIRA Project: key='{}', name='{}'", projectKey, projectName);

                Project existingProject = projectRepository.findByVerticalAndProjectKey(
                        vertical, projectKey);

                if (existingProject == null) {
                    Project project = new Project();
                    project.setVertical(vertical);
                    project.setProjectKey(projectKey);
                    project.setProjectName(projectName);
                    Project saved = projectRepository.save(project);
                    savedProjects.add(saved);
                    logger.info("Saved new Project: id={}, key='{}'", saved.getId(), saved.getProjectKey());
                } else {
                    existingProject.setProjectName(projectName);
                    Project updated = projectRepository.save(existingProject);
                    savedProjects.add(updated);
                    logger.info("Updated existing Project: id={}, key='{}'", updated.getId(), updated.getProjectKey());
                }
            }

            logger.info("Completed saving {} projects for Vertical ID {}", savedProjects.size(), verticalId);
            return ResponseEntity.ok(savedProjects);

        } catch (Exception e) {
            logger.error("Error connecting to JIRA for Vertical ID {}: {}", verticalId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body("Error connecting to Jira: " + e.getMessage());
        }
    }

    @GetMapping("/vertical/{verticalId}")
    public ResponseEntity<List<Project>> getProjectsByVertical(@PathVariable UUID verticalId) {
        logger.info("Fetching projects for Vertical ID: {}", verticalId);
        Optional<Vertical> verticalOpt = verticalRepository.findById(verticalId);

        if (!verticalOpt.isPresent()) {
            logger.warn("Vertical with ID '{}' not found when fetching projects", verticalId);
            return ResponseEntity.notFound().build();
        }

        List<Project> projects = projectRepository.findByVertical(verticalOpt.get());
        logger.info("Returning {} projects for Vertical ID {}", projects.size(), verticalId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable UUID id) {
        logger.info("Fetching project by ID: {}", id);
        Optional<Project> projectOpt = projectRepository.findById(id);

        if (projectOpt.isPresent()) {
            logger.info("Found project: id={}, key='{}'", projectOpt.get().getId(), projectOpt.get().getProjectKey());
            return ResponseEntity.ok(projectOpt.get());
        } else {
            logger.warn("Project with ID '{}' not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/issue/{verticalId}/{issueKey}")
    public ResponseEntity<JsonNode> getIssue(
            @PathVariable UUID verticalId,
            @PathVariable String issueKey,
            @RequestParam(required = false) String issueType) {
        Optional<Vertical> verticalOpt = verticalRepository.findById(verticalId);
        if (!verticalOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Vertical vertical = verticalOpt.get();
        JsonNode issue = jiraApiService.fetchIssueByKey(
                issueKey,
                vertical.getJiraServerUrl(),
                vertical.getJiraUsername(),
                vertical.getApiKey()); 
        ObjectNode filtered = objectMapper.createObjectNode();
        filtered.put("key", issue.path("key").asText());
        filtered.put("summary", issue.path("fields").path("summary").asText());
        filtered.set("description", issue.path("fields").path("description"));

        // Invoke AI service via gRPC
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
            .usePlaintext()
            .build();
        AiServiceGrpc.AiServiceBlockingStub aiStub = AiServiceGrpc.newBlockingStub(channel);
        AiRequest aiReq = AiRequest.newBuilder()
            .putParameters("summary", filtered.get("summary").asText())
            .putParameters("description", filtered.get("description").toString())
            .setTenantId(verticalId.toString())
            .build();
        AiResponse aiResp = aiStub.generateResponse(aiReq);
        channel.shutdown();
        ObjectNode result = objectMapper.createObjectNode();
        result.setAll(filtered);
        result.put("testCases", aiResp.getResponseText());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/issue/{verticalId}/search")
    public ResponseEntity<Map<String, Object>> searchIssuesByKeys(
            @PathVariable UUID verticalId,
            @RequestBody List<String> issueKeys,
            @RequestParam String issueType,
            @RequestParam(required = false) String templateName) {
        logger.info("[JiraProjectController] Received request to search issues by keys: {} with issueType: {}", issueKeys, issueType);
        
        // Validate issue type
        if (!isValidIssueType(issueType)) {
            logger.warn("[JiraProjectController] Invalid issue type: {}", issueType);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid issue type. Must be one of: Epic, Story, Bug");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        Optional<Vertical> verticalOpt = verticalRepository.findById(verticalId);
        if (!verticalOpt.isPresent()) {
            logger.warn("[JiraProjectController] Vertical not found with ID: {}", verticalId);
            return ResponseEntity.notFound().build();
        }
        
        logger.info("[JiraProjectController] Found vertical: {} with Jira URL: {}", verticalId, verticalOpt.get().getJiraServerUrl());
        Vertical vertical = verticalOpt.get();
        Map<String, Object> filteredResponse = new HashMap<>();
        
        // Special handling for Epic issue type
        if ("Epic".equalsIgnoreCase(issueType)) {
            logger.info("[JiraProjectController] Processing Epic issue type for keys: {}", issueKeys);
            
            for (String epicKey : issueKeys) {
                logger.info("[JiraProjectController] Processing Epic: {}", epicKey);
                try {
                    // First verify this is actually an Epic
                    logger.debug("[JiraProjectController] Fetching Epic details from Jira: {}", epicKey);
                    JsonNode epicIssue = jiraApiService.fetchIssueByKey(epicKey, 
                            vertical.getJiraServerUrl(),
                            vertical.getJiraUsername(),
                            vertical.getApiKey());
                    
                    String actualIssueType = epicIssue.path("fields").path("issuetype").path("name").asText("");
                    logger.info("[JiraProjectController] Actual issue type for {}: {}", epicKey, actualIssueType);
                    
                    if (!"Epic".equalsIgnoreCase(actualIssueType)) {
                        logger.warn("[JiraProjectController] Issue {} is not an Epic, it is: {}", epicKey, actualIssueType);
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("error", "Issue " + epicKey + " is not of type 'Epic', it is of type '" + actualIssueType + "'");
                        filteredResponse.put(epicKey, errorResponse);
                        continue;
                    }
                    
                    // Get all child issues of this Epic
                    logger.info("[JiraProjectController] Fetching child issues for Epic: {}", epicKey);
                    List<String> childIssueKeys = jiraApiService.getEpicChildren(epicKey,
                            vertical.getJiraServerUrl(),
                            vertical.getJiraUsername(),
                            vertical.getApiKey());
                    
                    if (childIssueKeys.isEmpty()) {
                        logger.info("[JiraProjectController] No child issues found for Epic {}", epicKey);
                        // Process the Epic itself if no children found
                        logger.info("[JiraProjectController] Processing the Epic itself since no children were found");
                        Map<String, Object> epicResponse = jiraApiService.searchIssuesByKeys(
                                vertical.getJiraServerUrl(),
                                vertical.getJiraUsername(),
                                vertical.getApiKey(),
                                Collections.singletonList(epicKey),
                                issueType,
                                templateName
                        );
                        
                        if (epicResponse.containsKey(epicKey) && epicResponse.get(epicKey) instanceof Map) {
                            Map<String, Object> epicData = (Map<String, Object>) epicResponse.get(epicKey);
                            if (epicData.containsKey("aiResponse")) {
                                logger.info("[JiraProjectController] Adding AI response for Epic: {}", epicKey);
                                filteredResponse.put(epicKey, Collections.singletonMap("aiResponse", epicData.get("aiResponse")));
                            } else {
                                logger.warn("[JiraProjectController] No AI response found for Epic: {}", epicKey);
                            }
                        }
                    } else {
                        // Process all child issues
                        logger.info("[JiraProjectController] Found {} child issues for Epic {}: {}", childIssueKeys.size(), epicKey, childIssueKeys);
                        
                        // Get child issue details and generate AI responses
                        logger.info("[JiraProjectController] Generating AI responses for child issues of Epic: {}", epicKey);
                        Map<String, Object> childrenResponses = jiraApiService.searchIssuesByKeys(
                                vertical.getJiraServerUrl(),
                                vertical.getJiraUsername(),
                                vertical.getApiKey(),
                                childIssueKeys,
                                "Story", // Assume children are stories
                                templateName
                        );
                        
                        // Create a map to store all child responses for this Epic
                        Map<String, Object> epicChildrenMap = new HashMap<>();
                        
                        // Process each child response
                        for (String childKey : childrenResponses.keySet()) {
                            logger.debug("[JiraProjectController] Processing child issue: {} for Epic: {}", childKey, epicKey);
                            Object childData = childrenResponses.get(childKey);
                            if (childData instanceof Map) {
                                Map<String, Object> childMap = (Map<String, Object>) childData;
                                if (childMap.containsKey("aiResponse")) {
                                    logger.debug("[JiraProjectController] Adding AI response for child issue: {}", childKey);
                                    epicChildrenMap.put(childKey, Collections.singletonMap("aiResponse", childMap.get("aiResponse")));
                                } else if (childMap.containsKey("error")) {
                                    logger.warn("[JiraProjectController] Error in child issue: {}: {}", childKey, childMap.get("error"));
                                    epicChildrenMap.put(childKey, childMap);
                                }
                            }
                        }
                        
                        // Add all children responses under the Epic key
                        logger.info("[JiraProjectController] Adding responses for {} child issues under Epic: {}", epicChildrenMap.size(), epicKey);
                        filteredResponse.put(epicKey, epicChildrenMap);
                    }
                } catch (Exception e) {
                    logger.error("[JiraProjectController] Error processing Epic {}: {}", epicKey, e.getMessage(), e);
                    filteredResponse.put(epicKey, Collections.singletonMap("error", e.getMessage()));
                }
            }
        } else {
            // Standard processing for non-Epic issue types
            logger.info("[JiraProjectController] Processing standard issue type: {} for keys: {}", issueType, issueKeys);
            Map<String, Object> fullResponse = jiraApiService.searchIssuesByKeys(
                    vertical.getJiraServerUrl(),
                    vertical.getJiraUsername(),
                    vertical.getApiKey(),
                    issueKeys,
                    issueType,
                    templateName
            );
            
            for (String issueKey : fullResponse.keySet()) {
                logger.debug("[JiraProjectController] Processing response for issue: {}", issueKey);
                Object issueData = fullResponse.get(issueKey);
                
                if (issueData instanceof Map) {
                    Map<String, Object> issueMap = (Map<String, Object>) issueData;
                    
                    // Check if the actual issue type matches the requested issue type
                    if (issueMap.containsKey("issueType") && !issueType.equalsIgnoreCase(issueMap.get("issueType").toString())) {
                        logger.warn("[JiraProjectController] Issue {} type mismatch. Requested: {}, Actual: {}", 
                                issueKey, issueType, issueMap.get("issueType"));
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("error", "Issue " + issueKey + " is not of type '" + issueType + 
                                          "', it is of type '" + issueMap.get("issueType") + "'");
                        filteredResponse.put(issueKey, errorResponse);
                    } else if (issueMap.containsKey("aiResponse")) {
                        logger.debug("[JiraProjectController] Adding AI response for issue: {}", issueKey);
                        // Only include the aiResponse in the filtered response
                        filteredResponse.put(issueKey, Collections.singletonMap("aiResponse", issueMap.get("aiResponse")));
                    } else if (issueMap.containsKey("error")) {
                        logger.warn("[JiraProjectController] Error in issue: {}: {}", issueKey, issueMap.get("error"));
                        // Keep error messages
                        filteredResponse.put(issueKey, issueMap);
                    }
                }
            }
        }
        
        logger.info("[JiraProjectController] Completed processing {} issues with type: {}", issueKeys.size(), issueType);
        return ResponseEntity.ok(filteredResponse);
    }
    
    /**
     * Validates if the provided issue type is one of the allowed types
     * @param issueType The issue type to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidIssueType(String issueType) {
        if (issueType == null || issueType.trim().isEmpty()) {
            return false;
        }
        
        List<String> validIssueTypes = Arrays.asList("Epic", "Story", "Bug");
        return validIssueTypes.stream()
                .anyMatch(type -> type.equalsIgnoreCase(issueType.trim()));
    }

    @GetMapping("/issues/{verticalId}")
    public ResponseEntity<Map<String, Object>> searchIssues(
            @PathVariable UUID verticalId,
            @RequestParam String jql,
            @RequestParam(required = false) String issueType,
            @RequestParam(required = false) String templateName) {
        Optional<Vertical> verticalOpt = verticalRepository.findById(verticalId);
        if (!verticalOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Vertical vertical = verticalOpt.get();
        Map<String, Object> resp = jiraApiService.searchIssues(
                vertical.getJiraServerUrl(),
                vertical.getJiraUsername(),
                vertical.getApiKey(),
                jql,
                issueType,
                templateName
        );
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/custom-api")
    public ResponseEntity<?> executeCustomJiraRequest(
            @RequestParam UUID verticalId,
            @RequestParam String endpoint,
            @RequestParam String method,
            @RequestBody(required = false) Map<String, Object> requestBody,
            @RequestParam(required = false) Map<String, String> queryParams) {

        logger.info("Executing custom Jira API request for vertical ID: {}, endpoint: {}", verticalId, endpoint);

        Optional<Vertical> verticalOpt = verticalRepository.findById(verticalId);
        if (!verticalOpt.isPresent()) {
            logger.warn("Vertical with ID '{}' not found", verticalId);
            return ResponseEntity.notFound().build();
        }

        Vertical vertical = verticalOpt.get();

        try {
            org.springframework.http.HttpMethod httpMethod = org.springframework.http.HttpMethod.valueOf(method.toUpperCase());

            Map<String, Object> response = jiraApiService.makeCustomRequest(
                    vertical.getJiraServerUrl(),
                    vertical.getJiraUsername(),
                    vertical.getApiKey(),
                    endpoint,
                    httpMethod,
                    requestBody,
                    queryParams
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid HTTP method: {}", method, e);
            return ResponseEntity.badRequest().body("Invalid HTTP method: " + method);
        } catch (Exception e) {
            logger.error("Error executing custom Jira request: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error executing custom Jira request: " + e.getMessage());
        }
    }
}


//