package com.sdet.sdet360.tenant.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "projects")
public class Project extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "vertical_id")
    private Vertical vertical;
    
    @Column(name = "project_key")
    private String projectKey;
    
    @Column(name = "project_name")
    private String projectName;
}
