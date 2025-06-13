package com.sdet.sdet360.tenant.service;

import com.sdet.sdet360.grpc.generated.AiRequest;
import com.sdet.sdet360.grpc.generated.AiResponse;
import com.sdet.sdet360.grpc.generated.AiServiceGrpc;
import com.sdet.sdet360.tenant.dto.PromptAutomationResponse;
import com.sdet.sdet360.tenant.dto.PromptRequest;
import com.sdet.sdet360.tenant.dto.PromptStep;
import com.sdet.sdet360.tenant.model.PromptAutomationTestCase;
import com.sdet.sdet360.tenant.model.PromptAutomationTestStep;
import com.sdet.sdet360.tenant.model.Feature;
import com.sdet.sdet360.tenant.repository.FeatureRepository;
import com.sdet.sdet360.tenant.repository.PromptAutomationTestCaseRepository;
import com.sdet.sdet360.tenant.repository.PromptAutomationTestStepRepository;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.UUID;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class PromptBasedAutomationService {

    private static final String BASE_DIRECTORY = System.getProperty("user.home") + "/sdet360/PBA_scriptsAndReports";

    @Autowired
    private PromptAutomationTestCaseRepository testCaseRepo;

    @Autowired
    private PromptAutomationTestStepRepository testStepRepo;

    @Autowired
    private FeatureRepository featureRepository;

    public PromptAutomationResponse generateAndRunScript(UUID verticalId, PromptRequest request, String host, int port) {
        String vertical = verticalId.toString();
        String selectedTool = request.getTool();
        String testCaseId = request.getTestCaseId();
        String category = request.getCategory();
        String description = request.getDescription();
        List<PromptStep> steps = request.getSteps();
        String userPrompt = buildPromptFromSteps(steps);

        String url = request.getUrl();
        if (url == null) {
            throw new IllegalArgumentException("No valid URL found in prompt.");
        }

        String html = fetchHtml(url);
        String formattedLocators = extractFormattedLocators(html);

        // Generate script via grpc
        String scriptContent  = callPromptBasedAutomation(userPrompt, formattedLocators, selectedTool, host, port);
        String scriptPath = saveScriptToFile(scriptContent, testCaseId, selectedTool);

        String executionStatus;
        switch (selectedTool.toLowerCase()){
            case "robot":
                executionStatus = executeRobotScript(scriptPath, testCaseId);
                break;

            case "selenium":
            case "selenium-python":
                executionStatus = executeSeleniumPythonScript(scriptPath, testCaseId);
                break;

            case "selenium-java":
                String projectPath = System.getProperty("user.dir");;
                String mainClassName = "com.sdet.sdet360.Sdet360Application";
                executionStatus = executeSeleniumJava(projectPath, mainClassName, testCaseId);
                break;
            default:
                throw new IllegalArgumentException("Unsupported automation tool: " + selectedTool);
        }

        // Save to DB
        Feature feature = featureRepository.findByFeatureName(vertical)
                .orElseGet(() -> {
                    Feature newFeature = new Feature();
                    newFeature.setFeatureName(vertical);
                    return featureRepository.save(newFeature);
                });

        PromptAutomationTestCase testCase = new PromptAutomationTestCase();
        testCase.setFeature(feature);
        testCase.setTestCaseId(testCaseId);
        testCase.setCategory(category);
        testCase.setDescription(description);
        testCase.setLogPath(BASE_DIRECTORY + "/" + testCaseId + "/log.html");
        testCase.setReportPath(BASE_DIRECTORY + "/" + testCaseId + "/report.html");
        testCase.setOutputPath(BASE_DIRECTORY + "/" + testCaseId + "/output.xml");

        testCase = testCaseRepo.save(testCase);

        int stepNo = 1;
        for (PromptStep step : steps) {
            PromptAutomationTestStep testStep = new PromptAutomationTestStep();
            testStep.setTestCase(testCase);
            testStep.setStepNumber(stepNo++);
            testStep.setTestStep(step.getTestStep());
            testStep.setTestData(step.getTestData());
            testStep.setExpectedResult(step.getExpectedResult());
            testStepRepo.save(testStep);
        }

        return new PromptAutomationResponse(
                executionStatus.toLowerCase().contains("success"),
                executionStatus
        );
    }

    public String buildPromptFromSteps(List<PromptStep> steps) {
        StringBuilder prompt = new StringBuilder();
        for (PromptStep step : steps) {
            prompt.append(" ").append(step.getTestStep())
                    .append(" ").append(step.getTestData());
            if (step.getExpectedResult() != null && !step.getExpectedResult().isBlank()) {
                prompt.append(" ").append(step.getExpectedResult());
            }
            prompt.append(", ");
        }
        return prompt.toString().trim();
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
    private String callPromptBasedAutomation(String userPrompt, String formattedLocators, String selectedTool,
                                             String host, int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        AiServiceGrpc.AiServiceBlockingStub stub = AiServiceGrpc.newBlockingStub(channel);

        AiRequest req = AiRequest.newBuilder()
                .putParameters("user_prompt", userPrompt)
                .putParameters("formatted_locators", formattedLocators)
                .putParameters("tool", selectedTool)
                .build();

        AiResponse response = stub.promptBasedAutomation(req);
        channel.shutdown();

        String result = response.getResponseText();
        if (result == null || result.trim().isEmpty()) {
            throw new RuntimeException("gRPC response text is null or empty");
        }

        return result;
    }

    private String saveScriptToFile(String scriptContent, String testCaseId, String tool) {
        String testCaseDir = BASE_DIRECTORY + "/" + testCaseId;
        try {
            File dir = new File(testCaseDir);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + testCaseDir);
            }

            String extension;

            if (tool.equalsIgnoreCase("robot")) {
                extension = ".robot";
            } else if (tool.equalsIgnoreCase("selenium-python")) {
                extension = ".py";
            } else if (tool.equalsIgnoreCase("selenium-java")) {
                extension = ".java";
            } else {
                throw new IllegalArgumentException("Unsupported tool for script saving: " + tool);
            }
            File scriptFile = new File(dir, "generated_script_" + testCaseId + extension);
            try (FileWriter writer = new FileWriter(scriptFile)) {
                writer.write(scriptContent);
            }

            return scriptFile.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write script file", e);
        }
    }

    private String executeRobotScript(String scriptPath, String testCaseId) {
        String outputDir = BASE_DIRECTORY + "/" + testCaseId;

        try {
            File dir = new File(outputDir);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new RuntimeException("Failed to create output directory: " + outputDir);
            }

            ProcessBuilder pb = new ProcessBuilder("robot", "--outputdir", outputDir, scriptPath);
            pb.inheritIO();
            Process process = pb.start();
            boolean finished = process.waitFor(120, TimeUnit.SECONDS);

            if (!finished) return "Execution timed out";

            int code = process.exitValue();
            return code == 0 ?
                    "Script executed successfully." :
                    "Script failed with exit code: " + code;

        } catch (Exception e) {
            throw new RuntimeException("Script execution error", e);
        }
    }

    private String executeSeleniumPythonScript(String scriptPath, String testCaseId) {
        String outputDir = BASE_DIRECTORY + "/" + testCaseId;

        try {
            File dir = new File(outputDir);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new RuntimeException("Failed to create output directory: " + outputDir);
            }

            ProcessBuilder pb = new ProcessBuilder("python", scriptPath);
            pb.directory(new File(outputDir));
            pb.inheritIO();
            Process process = pb.start();
            boolean finished = process.waitFor(120, TimeUnit.SECONDS);

            if (!finished) return "Execution timed out";

            int code = process.exitValue();
            return code == 0 ?
                    "Script executed successfully." :
                    "Script failed with exit code: " + code;

        } catch (Exception e) {
            throw new RuntimeException("Script execution error", e);
        }
    }

    private String executeSeleniumJava(String projectPath, String mainClassName, String testCaseId) {
        String outputDir = BASE_DIRECTORY + "/" + testCaseId;

        try {
            File dir = new File(outputDir);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new RuntimeException("Failed to create output directory: " + outputDir);
            }

            // Step: Run Maven command to compile and execute the class with main method
            ProcessBuilder pb = new ProcessBuilder(
                    "mvn",
                    "compile",
                    "exec:java",
                    "-Dexec.mainClass=" + mainClassName
            );

            pb.directory(new File(projectPath)); // Project directory where pom.xml exists
            pb.inheritIO(); // Log Maven output to console
            Process process = pb.start();
            boolean finished = process.waitFor(120, TimeUnit.SECONDS);

            if (!finished) return "Execution timed out";

            int code = process.exitValue();
            return code == 0 ?
                    "Script executed successfully." :
                    "Script failed with exit code: " + code;

        } catch (Exception e) {
            throw new RuntimeException("Script execution error", e);
        }
    }


    public File createZipForTestCase(String testCaseId) {
        String testCaseDirPath = BASE_DIRECTORY + "/" + testCaseId;
        File testCaseDir = new File(testCaseDirPath);

        if (!testCaseDir.exists() || !testCaseDir.isDirectory()) {
            throw new RuntimeException("Test case folder does not exist for ID: " + testCaseId);
        }

        File zipFile = new File(BASE_DIRECTORY + "/" + testCaseId + ".zip");

        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            zipFolder(testCaseDir, testCaseDir.getName(), zos);

        } catch (IOException e) {
            throw new RuntimeException("Error zipping test case folder", e);
        }

        return zipFile;
    }

    private void zipFolder(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                zipFolder(file, parentFolder + "/" + file.getName(), zos);
            } else {
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(parentFolder + "/" + file.getName());
                    zos.putNextEntry(zipEntry);

                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        zos.write(bytes, 0, length);
                    }

                    zos.closeEntry();
                }
            }
        }
    }

}
