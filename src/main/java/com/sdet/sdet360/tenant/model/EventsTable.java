package com.sdet.sdet360.tenant.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "event_tables")
public class EventsTable extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "interaction_id")
    private InteractionTable interaction;
    
    @Column(name = "absolute_path")
    private String absolutePath;
    
    @Column(name = "relative_xpath")
    private String relativeXpath;
    
    @Column(name = "relational_xpath")
    private String relationalXpath;
    
    @Column(name = "is_modified")
    private Boolean isModified;
  }
