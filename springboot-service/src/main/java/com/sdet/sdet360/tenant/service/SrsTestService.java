package com.sdet.sdet360.tenant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sdet.sdet360.grpc.generated.AiRequest;
import com.sdet.sdet360.grpc.generated.AiResponse;
import com.sdet.sdet360.grpc.generated.AiServiceGrpc;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
 
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets; 

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Service
public class SrsTestService {

    private static final Logger logger = LoggerFactory.getLogger(SrsTestService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${grpc.server.host:localhost}")
    private String grpcServerHost;

    @Value("${grpc.server.port:50051}")
    private int grpcServerPort;

    /**
     * Process a document file and extract its content
     * @param file The uploaded file (PDF, TXT, DOC, DOCX)
     * @return JsonNode containing file information and extracted content
     * @throws IOException If file processing fails
     */
    public JsonNode processDocument(MultipartFile file) throws IOException {
        String content = extractTextFromDocument(file);
 
        ObjectNode responseJson = objectMapper.createObjectNode();
        responseJson.put("fileName", file.getOriginalFilename());
        responseJson.put("fileSize", file.getSize());
        responseJson.put("contentLength", content.length());
        responseJson.put("content", content);

        return responseJson;
    }

    /**
     * Process a document file and generate Jira stories from its content
     * @param file The uploaded file (PDF, TXT, DOC, DOCX)
     * @return JsonNode containing file information, extracted content and generated Jira stories
     * @throws IOException If file processing fails
     */
    public JsonNode processDocumentWithJiraStories(MultipartFile file) throws IOException {
        String content = extractTextFromDocument(file);
 
        String jiraStories = generateJiraStories(content);

        ObjectNode responseJson = objectMapper.createObjectNode();
        responseJson.put("fileName", file.getOriginalFilename());
        responseJson.put("fileSize", file.getSize());
        responseJson.put("contentLength", content.length());
        responseJson.put("content", content);
        responseJson.put("jiraStories", jiraStories);

        return responseJson;
    }
    
    /**
     * Legacy method for backward compatibility
     */
    public JsonNode processSrsPdf(MultipartFile file) throws IOException {
        return processDocument(file);
    }

    /**
     * Legacy method for backward compatibility
     */
    public JsonNode processSrsPdfWithJiraStories(MultipartFile file) throws IOException {
        return processDocumentWithJiraStories(file);
    }

    /**
     * Extract text from a document based on its content type
     * @param file The uploaded file
     * @return The extracted text content
     * @throws IOException If text extraction fails
     */
    private String extractTextFromDocument(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        
        logger.info("Processing file: {}, content type: {}", fileName, contentType);
         if (contentType != null) {
            if (contentType.equals("application/pdf")) {
                return extractTextFromPdf(file);
            } else if (contentType.equals("text/plain")) {
                return extractTextFromTextFile(file);
            } else if (contentType.equals("application/msword") || 
                       contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                return extractTextFromWordDocument(file);
            }
        }
        
         if (fileName != null && fileName.contains(".")) {
            String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            if ("pdf".equals(extension)) {
                return extractTextFromPdf(file);
            } else if ("txt".equals(extension)) {
                return extractTextFromTextFile(file);
            } else if ("doc".equals(extension) || "docx".equals(extension)) {
                return extractTextFromWordDocument(file);
            }
        }
        
        logger.error("Unsupported file type: {}, filename: {}", contentType, fileName);
        throw new IOException("Unsupported file type. Only PDF, TXT, DOC, and DOCX files are supported.");
    }

    /**
     * Extract text from a PDF file
     * @param file The PDF file
     * @return The extracted text
     * @throws IOException If text extraction fails
     */
    private String extractTextFromPdf(MultipartFile file) throws IOException {
        logger.info("Extracting text from PDF file: {}", file.getOriginalFilename());
        PDDocument document = null;
        try {
            byte[] pdfBytes = file.getBytes();
            document = Loader.loadPDF(pdfBytes);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            logger.info("Successfully extracted text from PDF file: {}, content length: {}", 
                    file.getOriginalFilename(), text.length());
            return text;
        } catch (IOException e) {
            logger.error("Failed to extract text from PDF file: {}", e.getMessage(), e);
            throw new IOException("Failed to extract text from PDF: " + e.getMessage(), e);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    logger.warn("Error closing PDF document: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * Extract text from a plain text file
     * @param file The text file
     * @return The extracted text
     * @throws IOException If text extraction fails
     */
    private String extractTextFromTextFile(MultipartFile file) throws IOException {
        try {
            byte[] bytes = file.getBytes();
            String content = new String(bytes, StandardCharsets.UTF_8);
            logger.info("Successfully extracted text from text file: {}, content length: {}", 
                    file.getOriginalFilename(), content.length());
            return content;
        } catch (IOException e) {
            logger.error("Failed to extract text from text file: {}", e.getMessage(), e);
            throw new IOException("Failed to extract text from text file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extract text from a Word document (DOC or DOCX)
     * @param file The Word document file
     * @return The extracted text
     * @throws IOException If text extraction fails
     */
    private String extractTextFromWordDocument(MultipartFile file) throws IOException {
        logger.info("Extracting text from Word document: {}", file.getOriginalFilename());
        InputStream inputStream = null;
        String text = null;
        
        try {
            byte[] bytes = file.getBytes();
            String fileName = file.getOriginalFilename();
            boolean isDocx = fileName != null && fileName.toLowerCase().endsWith(".docx");
            
            inputStream = new ByteArrayInputStream(bytes);
            
            if (isDocx) { 
                try {
                    XWPFDocument document = new XWPFDocument(inputStream);
                    XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                    text = extractor.getText();
                    extractor.close();
                    document.close();
                    logger.info("Successfully extracted text from DOCX file using XWPFWordExtractor");
                } catch (Exception e) {
                    logger.warn("Error extracting text from DOCX with XWPFWordExtractor: {}", e.getMessage());
                    throw e;
                }
            } else { 
                try {
                    HWPFDocument document = new HWPFDocument(inputStream);
                    WordExtractor extractor = new WordExtractor(document);
                    text = extractor.getText();
                    extractor.close();
                    logger.info("Successfully extracted text from DOC file using WordExtractor");
                } catch (Exception e) {
                    logger.warn("Error extracting text from DOC with WordExtractor: {}", e.getMessage());
                    throw e;
                }
            }
            
            logger.info("Successfully extracted text from Word document: {}, content length: {}", 
                    file.getOriginalFilename(), text.length());
            return text;
            
        } catch (Exception e) {
            logger.error("Failed to extract text from Word document: {}", e.getMessage(), e);
            throw new IOException("Failed to extract text from Word document: " + e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.warn("Error closing input stream: {}", e.getMessage());
                }
            }
        }
    }

    private String generateJiraStories(String srsText) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(grpcServerHost, grpcServerPort)
                .usePlaintext().build();
        AiServiceGrpc.AiServiceBlockingStub stub = AiServiceGrpc.newBlockingStub(channel);

        try {
            AiRequest req = AiRequest.newBuilder()
                    .putParameters("srs_text", srsText)
                    .build();

            AiResponse resp = stub.generateJiraStories(req);
            return resp.getResponseText();
        } finally { 
            try {
                channel.shutdown();
                if (!channel.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                    channel.shutdownNow();
                }
            } catch (InterruptedException e) {
                channel.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}