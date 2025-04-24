package com.sdet.sdet360.tenant.model;

import jakarta.persistence.*;

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
