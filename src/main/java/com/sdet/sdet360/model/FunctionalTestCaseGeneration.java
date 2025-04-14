package com.sdet.sdet360.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "functional_test_case_generation")
public class FunctionalTestCaseGeneration extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "feature_id")
    private Feature feature;
    
    @Column(name = "test_name")
    private String testName;
    
    @Column(name = "test_description", columnDefinition = "text")
    private String testDescription;
    
    @Column(name = "test_priority")
    private String testPriority;
    
    @Column(name = "test_steps", columnDefinition = "jsonb")
    private String testSteps;
    
    @Column(name = "expected_results", columnDefinition = "jsonb")
    private String expectedResults;
}
