package com.sdet.sdet360.tenant.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "event_tables")
@AttributeOverride(name = "id", column = @Column(name = "event_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "action")
    private String Action;
    @Column(name = "type")
    private String Type;
    @Column(name = "value")
    private String Value;
    @Column(name = "assertion")
    private Boolean Assertion;
    @Column(name = "assertion_status")
    private Boolean assertionStatus;
    @Column(name = "auto_healed")
    private Boolean autoHealed;
    @Column(name = "is_modified")
    private Boolean isModified;
    
    // Add sequence number to maintain the original order of events
    @Column(name = "sequence_number")
    private Integer sequenceNumber;
}

