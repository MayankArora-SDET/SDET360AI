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

    @GetMapping("/issue/{verticalId}/{issueKey}/search")
    public ResponseEntity<Map<String, Object>> searchIssueByKey(
            @PathVariable UUID verticalId,
            @PathVariable String issueKey,
            @RequestParam String issueType,
            @RequestParam(required = false) String templateName) {
        Optional<Vertical> verticalOpt = verticalRepository.findById(verticalId);
        if (!verticalOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Vertical vertical = verticalOpt.get(); 
        String jql = "key=" + issueKey;
        if (issueType != null && !issueType.isBlank()) {
            jql += " AND issuetype=\"" + issueType + "\"";
        }
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