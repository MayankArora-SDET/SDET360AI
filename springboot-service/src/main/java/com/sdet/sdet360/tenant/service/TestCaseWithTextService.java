package com.sdet.sdet360.tenant.service;

import com.sdet.sdet360.grpc.FastApiGrpcClient;
import com.sdet.sdet360.grpc.generated.AiRequest;
import com.sdet.sdet360.grpc.generated.AiResponse;
import com.sdet.sdet360.grpc.generated.AiServiceGrpc;
import com.sdet.sdet360.tenant.model.TestCaseWithText;
import com.sdet.sdet360.tenant.repository.TestCaseWithTextRepository;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestCaseWithTextService {

    private final TestCaseWithTextRepository testCaseWithTextRepository;
    private final FastApiGrpcClient grpcClient;

    /**
     * Generates test cases based on the provided text and saves the result
     * 
     * @param verticalId The ID of the vertical
     * @param userText The text to generate test cases from
     * @return The generated test cases and saved entity
     */
    @Transactional
    public Map<String, Object> generateTestCasesWithText(UUID verticalId, String userText) {
        try {
            log.info("Generating test case for vertical {} with text: {}", verticalId, userText);
            
            ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext().build();
            AiServiceGrpc.AiServiceBlockingStub stub = AiServiceGrpc.newBlockingStub(channel);
            AiRequest req = AiRequest.newBuilder()
                    .putParameters("text", userText)
                    .build();
            AiResponse resp = stub.generateResponseForCodeGeneratorWithText(req);
            channel.shutdown();
           
            // Check if response is valid
            if (resp == null) {
                throw new IllegalStateException("No response received from AI service");
            }
            
            String responseText = resp.getResponseText();
            log.debug("Received AI response of length: {}", 
                    responseText != null ? responseText.length() : 0);
            
            // Create and save the test case
            TestCaseWithText testCaseWithText = new TestCaseWithText();
            testCaseWithText.setUserText(userText);
            testCaseWithText.setAiResponse(responseText);
            
            TestCaseWithText savedTestCase = testCaseWithTextRepository.save(testCaseWithText);
            log.info("Saved test case with ID: {}", savedTestCase.getId());
            
            // Prepare the response
            Map<String, Object> result = new HashMap<>();
            result.put("testCaseId", savedTestCase.getId());
            result.put("verticalId", verticalId);
            result.put("response", savedTestCase.getAiResponse());
            result.put("userText", savedTestCase.getUserText());
            result.put("createdAt", savedTestCase.getCreatedAt());
            
            return result;
        } catch (Exception e) {
            log.error("Error generating test cases: {}", e.getMessage(), e);
            throw e;
        }
    }
}
