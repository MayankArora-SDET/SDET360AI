package com.sdet.sdet360.tenant.controller;

import com.sdet.sdet360.tenant.dto.PromptRequest;
import com.sdet.sdet360.tenant.dto.PromptAutomationResponse;
import com.sdet.sdet360.tenant.service.PromptBasedAutomationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

@RestController
@RequestMapping("/api/automation/{verticalId}/testcase")
public class PromptBasedAutomationController {

    @Autowired
    private PromptBasedAutomationService automationService;

    @PostMapping("/generate_robot_script")
    public ResponseEntity<PromptAutomationResponse> generateRobotScriptFromPrompt(
            @PathVariable UUID verticalId,
//            @PathVariable String testCaseId,
            @RequestBody PromptRequest request,
            @RequestParam(defaultValue = "localhost") String host,
            @RequestParam(defaultValue = "50051") int port) {

//        String prompt = request.getPrompt();

        try {
            PromptAutomationResponse response =
                    automationService.generateAndRunRobotScript(verticalId, request, host, port);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Error response still in JSON
            PromptAutomationResponse errorResponse = new PromptAutomationResponse(
                    false,
                    "Script generation failed: " + e.getMessage()
            );
            return ResponseEntity.ok(errorResponse);
        }
    }

    @GetMapping("/download-test-case")
    public ResponseEntity<?> downloadTestCaseArtifacts(
            @PathVariable UUID verticalId,
            @RequestParam String testCaseId) {
        try {
            File zipFile = automationService.createZipForTestCase(testCaseId);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFile));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipFile.getName())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(zipFile.length())
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Download failed: " + e.getMessage());
        }
    }
}
