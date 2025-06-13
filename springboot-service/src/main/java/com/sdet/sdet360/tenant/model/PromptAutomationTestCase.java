package com.sdet.sdet360.tenant.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "prompt_automation_test_cases")
public class PromptAutomationTestCase extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "feature_id")
    private Feature feature;

    @Column(name = "test_case_id")
    private String testCaseId;

    @Column(name = "category")
    private String category;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "log_path", columnDefinition = "text")
    private String logPath;

    @Column(name = "report_path", columnDefinition = "text")
    private String reportPath;

    @Column(name = "output_path", columnDefinition = "text")
    private String outputPath;

    @OneToMany(mappedBy = "testCase", cascade = CascadeType.ALL)
    private List<PromptAutomationTestStep> steps;
}