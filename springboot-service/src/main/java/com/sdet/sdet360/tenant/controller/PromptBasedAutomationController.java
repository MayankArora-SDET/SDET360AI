package com.sdet.sdet360.tenant.controller;

import com.sdet.sdet360.tenant.dto.PromptRequest;
import com.sdet.sdet360.tenant.dto.PromptAutomationResponse;
import com.sdet.sdet360.tenant.service.PromptBasedAutomationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/automation/{verticalId}/testcase/{testCaseId}")
public class PromptBasedAutomationController {

    @Autowired
    private PromptBasedAutomationService automationService;

    @PostMapping("/generate_robot_script")
    public ResponseEntity<PromptAutomationResponse> generateRobotScriptFromPrompt(
            @PathVariable UUID verticalId,
            @PathVariable String testCaseId,
            @RequestBody PromptRequest request,
            @RequestParam(defaultValue = "localhost") String host,
            @RequestParam(defaultValue = "50051") int port) {

        String prompt = request.getPrompt();

        try {
            // Service method returns DTO now
            PromptAutomationResponse response = automationService.generateAndRunRobotScript(verticalId, prompt, host, port, testCaseId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Error response still in JSON
            PromptAutomationResponse errorResponse = new PromptAutomationResponse(
                    false,
                    "Script generation failed: " + e.getMessage(),
                    testCaseId,
                    null,
                    null,
                    null
            );
            return ResponseEntity.ok(errorResponse);
        }
    }
}
