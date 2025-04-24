package com.sdet.sdet360.tenant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "functional_database_testing")
public class FunctionalDatabaseTesting extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "feature_id")
    private Feature feature;
    
    @Column(name = "test_name")
    private String testName;
    
    @Column(name = "sql_query", columnDefinition = "text")
    private String sqlQuery;
    
    @Column(name = "expected_results", columnDefinition = "jsonb")
    private String expectedResults;}
