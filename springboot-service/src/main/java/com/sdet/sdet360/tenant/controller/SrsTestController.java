package com.sdet.sdet360.tenant.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.sdet.sdet360.tenant.service.SrsTestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/srs")
public class SrsTestController {

    private final SrsTestService srsService;

    public SrsTestController(SrsTestService srsService) {
        this.srsService = srsService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadSrsPdf(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please upload a file");
            }

            if (!file.getContentType().equals(MediaType.APPLICATION_PDF_VALUE)) {
                return ResponseEntity.badRequest().body("Only PDF files are allowed");
            }

            JsonNode srsDocument = srsService.processSrsPdf(file);

            return ResponseEntity.ok(srsDocument);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process SRS PDF: " + e.getMessage());
        }
    }

    @PostMapping("/generate-jira-stories")
    public ResponseEntity<?> generateJiraStories(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please upload a file");
            }

            // Check if file is a PDF
            if (!file.getContentType().equals(MediaType.APPLICATION_PDF_VALUE)) {
                return ResponseEntity.badRequest().body("Only PDF files are allowed");
            }

            // Process the file and generate Jira stories
            JsonNode result = srsService.processSrsPdfWithJiraStories(file);

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate Jira stories from SRS PDF: " + e.getMessage());
        }
    }
}