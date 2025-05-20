package com.sdet.sdet360.tenant.controller;

import com.sdet.sdet360.config.TenantContextHolder;
import com.sdet.sdet360.tenant.model.Vertical;
import com.sdet.sdet360.tenant.repository.VerticalRepository;
import com.sdet.sdet360.tenant.service.TestCaseWithTextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test-cases")
public class TestCaseGeneratorController {

    private final TestCaseWithTextService testCaseWithTextService;
    private final VerticalRepository verticalRepository;

    /**
     * Generate test cases based on input text
     * 
     * @param verticalId The ID of the vertical
     * @param request Map containing the text to generate test cases from
     * @return Map containing the generated test cases and metadata
     */
    @PostMapping("/{verticalId}/generate")
    public ResponseEntity<Map<String, Object>> generateTestCasesWithText(
            @PathVariable UUID verticalId,
            @RequestBody Map<String, String> request) {
        
        // Verify vertical exists
        Optional<Vertical> verticalOpt = verticalRepository.findById(verticalId);
        if (!verticalOpt.isPresent()) {
            log.warn("Vertical not found: {}", verticalId);
            return ResponseEntity.notFound().build();
        }
        
        // Get text from request
        String userText = request.get("text");
        if (userText == null || userText.isEmpty()) {
            log.warn("Empty or missing text in request");
            return ResponseEntity.badRequest().body(Map.of("error", "Text is required"));
        }
        
        try {
            // Set tenant context
            TenantContextHolder.setTenantId(verticalId);
            
            // Generate test cases
            Map<String, Object> result = testCaseWithTextService.generateTestCasesWithText(
                    verticalId, userText);
            
            log.info("Successfully generated test cases for vertical {}", verticalId);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Error generating test cases: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to generate test cases: " + e.getMessage()));
        } finally {
            TenantContextHolder.clear();
        }
    }
}
