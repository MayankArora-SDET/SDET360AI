package com.sdet.sdet360.tenant.model;


import jakarta.persistence.*;

@Entity
@Table(name = "automation_custom_function")
public class AutomationCustomFunction extends BaseEntity {
    
    
    @ManyToOne
    @JoinColumn(name = "feature_id")
    private Feature feature;
    
    @Column(name = "function_name")
    private String functionName;
    
    @Column(name = "function_code", columnDefinition = "text")
    private String functionCode;
    
    @Column(name = "function_language")
    private String functionLanguage;
}