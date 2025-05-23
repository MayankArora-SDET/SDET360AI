 package com.sdet.sdet360.tenant.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "quality_objective_status")
@Data
public class QualityObjectiveStatus extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name="key_feature")
    private String keyFeature;
    
    @Column(nullable = false)
    private String category;
    
    @Column(name = "success_criteria_level_1", nullable = false)
    private String successCriteriaLevel1;
    
    @Column(name = "success_criteria_level_2", nullable = false)
    private String successCriteriaLevel2;
    
    @Column(nullable = false)
    private String status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vertical_id", nullable = false)
    private Vertical vertical;
}
