package com.sdet.sdet360.tenant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sdet.sdet360.grpc.generated.AiRequest;
import com.sdet.sdet360.grpc.generated.AiResponse;
import com.sdet.sdet360.grpc.generated.AiServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class SrsTestService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${grpc.server.host:localhost}")
    private String grpcServerHost;

    @Value("${grpc.server.port:50051}")
    private int grpcServerPort;

    public JsonNode processSrsPdf(MultipartFile file) throws IOException {
        String content = extractTextFromPdf(file);

        // Create a JSON response with the extracted content
        ObjectNode responseJson = objectMapper.createObjectNode();
        responseJson.put("fileName", file.getOriginalFilename());
        responseJson.put("fileSize", file.getSize());
        responseJson.put("contentLength", content.length());
        responseJson.put("content", content);

        return responseJson;
    }

    public JsonNode processSrsPdfWithJiraStories(MultipartFile file) throws IOException {
        String content = extractTextFromPdf(file);

        // Generate Jira stories from the content
        String jiraStories = generateJiraStories(content);

        // Create a JSON response with the extracted content and Jira stories
        ObjectNode responseJson = objectMapper.createObjectNode();
        responseJson.put("fileName", file.getOriginalFilename());
        responseJson.put("fileSize", file.getSize());
        responseJson.put("contentLength", content.length());
        responseJson.put("content", content);
        responseJson.put("jiraStories", jiraStories);

        return responseJson;
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        // Method 1: Using the byte array from the MultipartFile
        try {
            byte[] pdfBytes = file.getBytes();
            PDDocument document = Loader.loadPDF(pdfBytes);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();
            return text;
        } catch (IOException e) {
            throw new IOException("Failed to extract text from PDF: " + e.getMessage(), e);
        }
    }

    private String generateJiraStories(String srsText) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(grpcServerHost, grpcServerPort)
                .usePlaintext().build();
        AiServiceGrpc.AiServiceBlockingStub stub = AiServiceGrpc.newBlockingStub(channel);

        AiRequest req = AiRequest.newBuilder()
                .putParameters("srs_text", srsText)
                .build();

        AiResponse resp = stub.generateJiraStories(req);
        channel.shutdown();
        return resp.getResponseText();
    }
}