package com.sdet.sdet360.tenant.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "test_case_with_text")
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseWithText extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_id", nullable = true)
    private Feature feature;
    
    @Column(name = "user_text", columnDefinition = "TEXT")
    private String userText;
    
    @Column(name = "ai_response", columnDefinition = "TEXT")
    private String aiResponse;
}
