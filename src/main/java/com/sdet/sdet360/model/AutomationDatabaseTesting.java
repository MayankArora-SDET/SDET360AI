package com.sdet.sdet360.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "automation_database_testing")
public class AutomationDatabaseTesting extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "feature_id")
    private Feature feature;
    
    @Column(name = "test_name")
    private String testName;
    
    @Column(name = "query_config", columnDefinition = "jsonb")
    private String queryConfig;
    
    @Column(name = "expected_results", columnDefinition = "jsonb")
    private String expectedResults;
}
