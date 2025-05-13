package com.sdet.sdet360.tenant.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "features")
@AttributeOverride(name = "id", column = @Column(name = "feature_id"))
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

    public Feature() {
		
	}

	public Feature(Vertical vertical, String featureName, String featureType, Boolean isEnabled) {
		
		this.vertical = vertical;
		this.featureName = featureName;
		this.featureType = featureType;
		this.isEnabled = isEnabled;
	}
}