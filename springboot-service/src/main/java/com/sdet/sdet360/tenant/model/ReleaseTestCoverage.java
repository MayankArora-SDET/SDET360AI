package com.sdet.sdet360.tenant.model;
 
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "release_test_coverage")
@Data
public class ReleaseTestCoverage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(nullable = false)
    private String epic;
    
    @Column(name = "severity_1", nullable = false)
    private Integer severity1;
    
    @Column(name = "severity_2", nullable = false)
    private Integer severity2;
    
    @Column(name = "severity_3", nullable = false)
    private Integer severity3;
    
    @Column(name = "severity_4", nullable = false)
    private Integer severity4;
    
    @Column(name = "test_cases", nullable = false)
    private Integer testCases;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vertical_id", nullable = false)
    private Vertical vertical;
}
