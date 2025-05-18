package com.sdet.sdet360.tenant.service;

import com.sdet.sdet360.grpc.generated.AiRequest;
import com.sdet.sdet360.grpc.generated.AiResponse;
import com.sdet.sdet360.grpc.generated.AiServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

@Service
public class PromptBasedAutomationService {

    private static final String SCRIPT_DIRECTORY = "C:/Generate_script";

    /**
     * Main method to extract locators, send to Python gRPC server, generate script and run it.
     */
    public String generateAndRunRobotScript(String userPrompt, String host, int port) {
        String url = extractUrl(userPrompt);
        if (url == null) {
            throw new IllegalArgumentException("No valid URL found in prompt.");
        }

        String html = fetchHtml(url);
        String formattedLocators = extractFormattedLocators(html);

        // Generate the robot script content
        String robotScript = callPromptBasedAutomation(userPrompt, formattedLocators, host, port);

        // Save the script locally
        String scriptFilePath = saveScriptToFile(robotScript);

        // Run the script
        return executeRobotScript(scriptFilePath);
    }

    private String extractUrl(String prompt) {
        Matcher matcher = Pattern.compile("(https?://[^\\s,]+)").matcher(prompt);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String fetchHtml(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .get();
            return doc.html();
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch HTML: " + e.getMessage(), e);
        }
    }

    private String extractFormattedLocators(String html) {
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getAllElements();
        Map<String, String> locators = new LinkedHashMap<>();

        for (Element el : elements) {
            if (!isValidElement(el)) continue;
            String key = generateVariableName(el);
            String xpath = generateXPath(el);
            if (key != null && xpath != null) {
                locators.put(key, "xpath=" + xpath);
            }
        }

        return locators.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");
    }

    private boolean isValidElement(Element el) {
        Set<String> ignored = Set.of("script", "style", "meta", "link", "noscript");
        return !ignored.contains(el.tagName()) && !"hidden".equalsIgnoreCase(el.attr("type"));
    }

    private String generateXPath(Element el) {
        if (el.hasAttr("name")) return String.format("//%s[@name='%s']", el.tagName(), el.attr("name"));
        if (el.hasAttr("id")) return String.format("//%s[@id='%s']", el.tagName(), el.attr("id"));
        if (el.hasAttr("placeholder")) return String.format("//%s[@placeholder='%s']", el.tagName(), el.attr("placeholder"));
        if (el.hasAttr("aria-label")) return String.format("//%s[@aria-label='%s']", el.tagName(), el.attr("aria-label"));
        if (el.hasAttr("title")) return String.format("//%s[@title='%s']", el.tagName(), el.attr("title"));

        String className = el.className();
        if (!className.isBlank()) {
            return String.format("//%s[contains(@class, '%s')]", el.tagName(), className.split("\\s+")[0]);
        }

        for (org.jsoup.nodes.Attribute attr : el.attributes()) {
            if (attr.getKey().startsWith("data-")) {
                return String.format("//%s[@%s='%s']", el.tagName(), attr.getKey(), attr.getValue());
            }
        }

        return null;
    }

    private String generateVariableName(Element el) {
        for (String attr : List.of("name", "id", "placeholder", "aria-label", "title")) {
            String value = el.attr(attr);
            if (value != null && !value.isBlank()) return clean(value);
        }

        String classAttr = el.className();
        if (!classAttr.isBlank()) return clean(classAttr.split("\\s+")[0]);

        for (org.jsoup.nodes.Attribute attr : el.attributes()) {
            if (attr.getKey().startsWith("data-")) return clean(attr.getValue());
        }

        return null;
    }

    private String clean(String value) {
        return value.trim().toLowerCase().replaceAll("[^a-zA-Z0-9_]", "_");
    }

    /**
     * Makes the gRPC call to the AI microservice with raw prompt and extracted locators
     */
    private String callPromptBasedAutomation(String userPrompt, String formattedLocators,
                                             String host, int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        AiServiceGrpc.AiServiceBlockingStub stub = AiServiceGrpc.newBlockingStub(channel);

        AiRequest req = AiRequest.newBuilder()
                .putParameters("user_prompt", userPrompt)
                .putParameters("formatted_locators", formattedLocators)
                .build();

        AiResponse response = stub.promptBasedAutomation(req);
        channel.shutdown();

        String result = response.getResponseText();
        if (result == null || result.trim().isEmpty()) {
            throw new RuntimeException("gRPC response text is null or empty");
        }

        return result;
    }

    /**
     * Saves the generated Robot Framework script to a local file
     */
    private String saveScriptToFile(String scriptContent) {
        try {
            // Ensure directory exists
            File scriptDir = new File(SCRIPT_DIRECTORY);
            if (!scriptDir.exists()) {
                scriptDir.mkdirs();
            }

            // Generate a unique file name using UUID
            String filename = "generated_test_" + UUID.randomUUID().toString().substring(0, 8) + ".robot";
            File scriptFile = new File(scriptDir, filename);

            try (FileWriter writer = new FileWriter(scriptFile)) {
                writer.write(scriptContent);
            }

            return scriptFile.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save Robot Framework script to file", e);
        }
    }

    /**
     * Executes the Robot Framework script using the robot CLI
     */
    private String executeRobotScript(String scriptFilePath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("robot", scriptFilePath);
            Process process = processBuilder.start();

            boolean finished = process.waitFor(60, TimeUnit.SECONDS); // Wait for script execution to finish
            if (!finished) {
                return "Script execution timed out";
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                return "Script execution failed with exit code: " + exitCode + "\n" + scriptFilePath;
            }

            return "Script executed successfully. Output saved at: " + scriptFilePath;
        } catch (Exception e) {
            throw new RuntimeException("Error executing the Robot Framework script", e);
        }
    }
}
