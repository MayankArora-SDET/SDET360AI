package com.sdet.sdet360.tenant.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "interaction_tables")
public class InteractionTable extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "feature_id")
    private Feature feature;
    
    @Column(name = "testcase_id")
    private UUID testcaseId;
    
    @Column(name = "description", columnDefinition = "text")
    private String description;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "url")
    private String url;
}
