package com.sdet.sdet360.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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
