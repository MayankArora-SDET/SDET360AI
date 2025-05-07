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

    public InteractionTable(Feature feature, UUID testcaseId, String description, String category, String url) {
        this.feature = feature;
        this.testcaseId = testcaseId;
        this.description = description;
        this.category = category;
        this.url = url;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public UUID getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(UUID testcaseId) {
        this.testcaseId = testcaseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
