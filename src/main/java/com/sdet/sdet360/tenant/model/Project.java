package com.sdet.sdet360.tenant.model;


import jakarta.persistence.*;

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

    public Project() {
    }
//
//    public Project(){
//
//    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Vertical getVertical() {
        return vertical;
    }

    public void setVertical(Vertical vertical) {
        this.vertical = vertical;
    }
}
