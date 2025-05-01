package com.sdet.sdet360.tenant.controller;


import com.sdet.sdet360.grpc.FastApiGrpcClient;
import com.sdet.sdet360.grpc.generated.*;
import com.sdet.sdet360.config.TenantContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final FastApiGrpcClient grpcClient;
    private final AiServiceGrpc.AiServiceBlockingStub stub;

    public AiController(FastApiGrpcClient grpcClient, AiServiceGrpc.AiServiceBlockingStub stub) {
        this.grpcClient = grpcClient;
        this.stub = stub;
    }


    @GetMapping("/test")
    public String testGenerate(@RequestParam String prompt) {
        AiRequest request = AiRequest.newBuilder()
                .setPrompt(prompt)
                .setTenantId("tenant1")
                .build();
        AiResponse response = stub.generateResponse(request);
        return response.getResponseText();
    }

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateResponse(
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId) {

        try {
            if (tenantId != null) {
                TenantContextHolder.setTenantId(UUID.fromString(tenantId));
            }

            String prompt = (String) request.get("prompt");
            @SuppressWarnings("unchecked")
            Map<String, String> parameters = (Map<String, String>) request.getOrDefault("parameters", new HashMap<>());

            AiResponse response = grpcClient.generateResponse(prompt, parameters);

            Map<String, Object> result = new HashMap<>();
            result.put("text", response.getResponseText());
            result.put("confidence", response.getConfidenceScore());
            result.put("metadata", response.getMetadataMap());

            return ResponseEntity.ok(result);
        } finally {
            TenantContextHolder.clear();
        }
    }

    @PostMapping("/process-document")
    public ResponseEntity<Map<String, Object>> processDocument(
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId) {

        try {
            if (tenantId != null) {
                TenantContextHolder.setTenantId(UUID.fromString(tenantId));
            }

            String documentContent = (String) request.get("documentContent");
            String documentType = (String) request.get("documentType");

            DocumentResponse response = grpcClient.processDocument(documentContent, documentType);

            List<Map<String, Object>> extractions = response.getExtractionsList().stream()
                    .map(extraction -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("field", extraction.getFieldName());
                        map.put("value", extraction.getFieldValue());
                        map.put("confidence", extraction.getConfidence());
                        return map;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("processedContent", response.getProcessedContent());
            result.put("extractions", extractions);
            result.put("status", response.getStatus());

            return ResponseEntity.ok(result);
        } finally {
            TenantContextHolder.clear();
        }
    }
}