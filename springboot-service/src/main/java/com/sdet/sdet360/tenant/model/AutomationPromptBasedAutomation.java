package com.sdet.sdet360.tenant.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "automation_prompt_based_automation")
//@AttributeOverride(name = "id", column = @Column(name = "pba_id"))
public class AutomationPromptBasedAutomation extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "feature_id")
    private Feature feature;

    @Column(name = "test_case_id")
    private String testCaseId;

    @Column(name = "category")
    private String category;

    @Column(name = "user_prompt", columnDefinition = "text")
    private String userPrompt;

    @Column(name = "generated_script", columnDefinition = "text")
    private String generatedScript;

    @Column(name = "log_path", columnDefinition = "text")
    private String logPath;

    @Column(name = "report_path", columnDefinition = "text")
    private String reportPath;

    @Column(name = "output_path", columnDefinition = "text")
    private String outputPath;
}