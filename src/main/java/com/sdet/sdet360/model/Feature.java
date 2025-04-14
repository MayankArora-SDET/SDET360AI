package com.sdet.sdet360.model;
 

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "features")
public class Feature extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "vertical_id")
    private Vertical vertical;
    
    @Column(name = "feature_name")
    private String featureName;
    
    @Column(name = "feature_type")
    private String featureType;
    
    @Column(name = "is_enabled")
    private Boolean isEnabled;
 }