package com.sdet.sdet360.tenant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "product_business_requirement")
public class ProductBusinessRequirement extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "feature_id")
    private Feature feature;
    
    @Column(name = "requirement_name")
    private String requirementName;
    
    @Column(name = "requirement_description", columnDefinition = "text")
    private String requirementDescription;
    
    @Column(name = "requirement_priority")
    private String requirementPriority;
    
    @Column(name = "acceptance_criteria", columnDefinition = "jsonb")
    private String acceptanceCriteria;
}
