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
 
            String contentType = file.getContentType();
            String fileName = file.getOriginalFilename();
            
            boolean isSupported = isSupportedFileType(contentType, fileName);
            
            if (!isSupported) {
                return ResponseEntity.badRequest().body("Only PDF, TXT, DOC, and DOCX files are allowed");
            }

             JsonNode result = srsService.processDocumentWithJiraStories(file);

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate Jira stories from document: " + e.getMessage());
        }
    }
    
    /**
     * Check if the file type is supported for processing
     * @param contentType The content type of the file
     * @param fileName The name of the file
     * @return true if the file type is supported, false otherwise
     */
    private boolean isSupportedFileType(String contentType, String fileName) {
    
        if (contentType != null) {
            if (contentType.equals("application/pdf") || 
                contentType.equals("text/plain") || 
                contentType.equals("application/msword") || 
                contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                return true;
            }
        }
        
        if (fileName != null && fileName.contains(".")) {
            String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            return extension.equals("pdf") || 
                   extension.equals("txt") || 
                   extension.equals("doc") || 
                   extension.equals("docx");
        }
        
        return false;
    }
}