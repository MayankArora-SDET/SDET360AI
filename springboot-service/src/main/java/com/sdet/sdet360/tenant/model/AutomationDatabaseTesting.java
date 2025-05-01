package com.sdet.sdet360.tenant.model;

import jakarta.persistence.*;

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
