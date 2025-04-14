package com.sdet.sdet360.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ai_insights")
public class AiInsight extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
    
    @Column(name = "insight_type")
    private String insightType;
    
    @Column(name = "insight_data", columnDefinition = "jsonb")
    private String insightData;
    
    
}
