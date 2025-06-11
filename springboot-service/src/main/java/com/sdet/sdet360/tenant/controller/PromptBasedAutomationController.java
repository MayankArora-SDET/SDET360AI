package com.sdet.sdet360.tenant.controller;

import com.sdet.sdet360.tenant.dto.*;
import com.sdet.sdet360.tenant.model.*;
import com.sdet.sdet360.tenant.repository.*;
import com.sdet.sdet360.tenant.service.PromptBasedAutomationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

@RestController
@RequestMapping("/api/automation/{verticalId}/testcase")
public class PromptBasedAutomationController {

    @Autowired
    private PromptBasedAutomationService automationService;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private PromptAutomationTestCaseRepository testCaseRepo;

    @Autowired
    private PromptAutomationTestStepRepository testStepRepo;

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

    @GetMapping("/recorded-prompt-automation-test-cases")
    public ResponseEntity<List<PromptAutomationTestCaseDto>> getRecordedPromptAutomationTestCases(@PathVariable UUID verticalId) {
        String vertical = verticalId.toString();

        Optional<Feature> featureOpt = featureRepository.findByFeatureName(vertical);
        if (featureOpt.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        Feature feature = featureOpt.get();

        // fetch all test cases
        List<PromptAutomationTestCase> testCases = testCaseRepo.findAll();

        // filter test cases that belong to the found feature
        List<PromptAutomationTestCase> filteredCases = new ArrayList<>();
        for (PromptAutomationTestCase testCase : testCases) {
            if (testCase.getFeature().getId().equals(feature.getId())) {
                filteredCases.add(testCase);
            }
        }

        // prepare response list
        List<PromptAutomationTestCaseDto> response = filteredCases.stream().map(testCase -> {
            PromptAutomationTestCaseDto dto = new PromptAutomationTestCaseDto();
            dto.setTestCaseId(testCase.getTestCaseId());
            dto.setCategory(testCase.getCategory());
            dto.setDescription(testCase.getDescription());

            List<PromptAutomationTestStep> steps = testStepRepo.findByTestCase_Id(testCase.getId());
            List<PromptAutomationTestStepDto> stepDtos = steps.stream().map(step -> {
                PromptAutomationTestStepDto stepDto = new PromptAutomationTestStepDto();
                stepDto.setStepNumber(step.getStepNumber());
                stepDto.setTestStep(step.getTestStep());
                stepDto.setTestData(step.getTestData());
                stepDto.setExpectedResult(step.getExpectedResult());
                return stepDto;
            }).toList();

            dto.setSteps(stepDtos);
            return dto;
        }).toList();

        return ResponseEntity.ok(response);
    }
}
