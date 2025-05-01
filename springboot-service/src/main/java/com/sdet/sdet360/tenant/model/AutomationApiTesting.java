package com.sdet.sdet360.tenant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "automation_api_testing")
public class AutomationApiTesting extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "feature_id")
    private Feature feature;
    
    @Column(name = "test_name")
    private String testName;
    
    @Column(name = "test_config", columnDefinition = "jsonb")
    private String testConfig;
    
    @Column(name = "test_results", columnDefinition = "jsonb")
    private String testResults;
    
}