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
@Table(name = "interaction_tables")
@AttributeOverride(name = "id", column = @Column(name = "interaction_id"))
@NoArgsConstructor
@AllArgsConstructor
public class InteractionTable extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "feature_id")
    private Feature feature;
    
    @Column(name = "testcase_id")
    private UUID testcaseId;

    @Column(name = "tc_id")
    private String tcId;
    
    @Column(name = "description", columnDefinition = "text")
    private String description;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "url")
    private String url;

}
