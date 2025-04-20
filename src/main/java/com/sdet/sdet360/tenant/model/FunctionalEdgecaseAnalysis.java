package com.sdet.sdet360.tenant.model;
 

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "functional_edgecase_analysis")
public class FunctionalEdgecaseAnalysis extends BaseEntity {
    
    
    @ManyToOne
    @JoinColumn(name = "feature_id")
    private Feature feature;
    
    @Column(name = "case_name")
    private String caseName;
    
    @Column(name = "case_description", columnDefinition = "text")
    private String caseDescription;
    
    @Column(name = "severity_level")
    private String severityLevel;
    
    @Column(name = "test_parameters", columnDefinition = "jsonb")
    private String testParameters;
    
}
