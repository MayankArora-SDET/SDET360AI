package com.sdet.sdet360.tenant.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "functional_testdata_creation")
public class FunctionalTestdataCreation extends BaseEntity { 
    
    @ManyToOne
    @JoinColumn(name = "feature_id")
    private Feature feature;
    
    @Column(name = "data_name")
    private String dataName;
    
    @Column(name = "test_data", columnDefinition = "jsonb")
    private String testData;
    
    @Column(name = "data_type")
    private String dataType;
    
}