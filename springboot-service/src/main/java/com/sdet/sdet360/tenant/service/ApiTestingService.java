package com.sdet.sdet360.tenant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.sdet.sdet360.grpc.generated.AiRequest;
import com.sdet.sdet360.grpc.generated.AiResponse;
import com.sdet.sdet360.grpc.generated.AiServiceGrpc;

@Service
public class ApiTestingService {
    private static final Logger logger = LoggerFactory.getLogger(ApiTestingService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${ai.service.host:localhost}")
    private String aiServiceHost;

    @Value("${ai.service.port:50051}")
    private int aiServicePort;

    /**
     * Generate API testing scenarios using the AI service
     *
     * @param tenantId The tenant ID (vertical ID)
     * @param url The API URL
     * @param method The HTTP method
     * @param params The query parameters
     * @param headers The request headers
     * @param body The request body
     * @return Generated test scenarios
     */
    public Map<String, Object> generateApiTestScenarios(
            UUID tenantId,
            String url,
            String method,
            List<Map<String, Object>> params,
            List<Map<String, Object>> headers,
            Object body) {

        logger.info("Generating API test scenarios for URL: {}, Method: {}", url, method);
        ManagedChannel channel = null;
        try {
            // Convert objects to JSON strings for the gRPC request
            String paramsJson = objectMapper.writeValueAsString(params);
            String headersJson = objectMapper.writeValueAsString(headers);
            String bodyJson = body != null ? objectMapper.writeValueAsString(body) : "None";

            // Format the parameter details for the prompt
            StringBuilder paramDetails = new StringBuilder();
            for (Map<String, Object> param : params) {
                paramDetails.append("- ")
                        .append(param.get("key")).append(": ")
                        .append(param.get("value")).append(" (")
                        .append(param.get("description")).append(")\n");
            }

            // Format the header details for the prompt
            StringBuilder headerDetails = new StringBuilder();
            for (Map<String, Object> header : headers) {
                headerDetails.append("- ")
                        .append(header.get("key")).append(": ")
                        .append(header.get("value")).append(" (")
                        .append(header.get("description")).append(")\n");
            }

            // Create a gRPC channel to the AI service
            channel = ManagedChannelBuilder
                    .forAddress(aiServiceHost, aiServicePort)
                    .usePlaintext()
                    .build();

            // Create a blocking stub for the AI service
            AiServiceGrpc.AiServiceBlockingStub stub = AiServiceGrpc.newBlockingStub(channel);

            // Build the AI request
            AiRequest request = AiRequest.newBuilder()
                    .putParameters("url", url)
                    .putParameters("method", method)
                    .putParameters("params", paramsJson)
                    .putParameters("headers", headersJson)
                    .putParameters("body", bodyJson)
                    .putParameters("param_details", paramDetails.length() > 0 ? paramDetails.toString() : "None")
                    .putParameters("header_details", headerDetails.length() > 0 ? headerDetails.toString() : "None")
                    .setTenantId(tenantId.toString())
                    .build();

            // Call the AI service to generate API testing scenarios
            AiResponse response = stub.generateApiTestingScenarios(request);

            // Prepare the result map
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("url", url);
            result.put("method", method);
            result.put("params", params);
            result.put("headers", headers);
            if (body != null) {
                result.put("body", body);
            }
            result.put("testScenarios", response.getResponseText());

            // Try to parse the response as JSON if possible
            try {
                Object parsedResponse = objectMapper.readValue(response.getResponseText(), Object.class);
                result.put("parsedScenarios", parsedResponse);
            } catch (JsonProcessingException e) {
                logger.debug("Response is not valid JSON, using raw text");
            }

            return result;
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize request payload", e);
            throw new IllegalStateException("Unable to generate API test scenarios", e);
        } catch (Exception e) {
            logger.error("Error generating API test scenarios", e);
            throw new RuntimeException("AI service call failed", e);
        } finally {
            if (channel != null) {
                try {
                    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException ie) {
                    logger.warn("Channel shutdown interrupted", ie);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}