package com.sdet.sdet360.tenant.controller;

import com.sdet.sdet360.tenant.dto.PromptRequest;
import com.sdet.sdet360.tenant.service.PromptBasedAutomationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/automation/{verticalId}")
public class PromptBasedAutomationController {

    @Autowired
    private PromptBasedAutomationService automationService;

    @PostMapping(value = "/generate_robot_script", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> generateRobotScriptFromPrompt(
            @PathVariable UUID verticalId,
            @RequestBody PromptRequest request,
            @RequestParam(defaultValue = "localhost") String host,
            @RequestParam(defaultValue = "50051") int port)
    {

        String prompt = request.getPrompt();

        try {
            String robotScript = automationService.generateAndRunRobotScript(prompt, host, port);
            return ResponseEntity.ok(robotScript);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to generate script: " + e.getMessage());
        }
    }
}
