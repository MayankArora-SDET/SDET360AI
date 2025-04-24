package com.sdet.sdet360.tenant.model;


import jakarta.persistence.*;

@Entity
@Table(name = "features")
public class Feature extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "vertical_id")
    private Vertical vertical;
    
    @Column(name = "feature_name")
    private String featureName;
    
    @Column(name = "feature_type")
    private String featureType;
    
    @Column(name = "is_enabled")
    private Boolean isEnabled;

	public Vertical getVertical() {
		return vertical;
	}

	public void setVertical(Vertical vertical) {
		this.vertical = vertical;
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public String getFeatureType() {
		return featureType;
	}

	public void setFeatureType(String featureType) {
		this.featureType = featureType;
	}

	public Boolean getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	public Feature() {
		
	}

	public Feature(Vertical vertical, String featureName, String featureType, Boolean isEnabled) {
		
		this.vertical = vertical;
		this.featureName = featureName;
		this.featureType = featureType;
		this.isEnabled = isEnabled;
	}
    
    
 }