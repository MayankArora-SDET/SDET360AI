package com.sdet.sdet360.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "open_prompt")
public class OpenPrompt extends BaseEntity{
    
    @ManyToOne
    @JoinColumn(name = "feature_id")
    private Feature feature;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "message_content", columnDefinition = "text")
    private String messageContent;
    
    @Column(name = "message_type")
    private String messageType;
    
    @Column(name = "message_time")
    private LocalDateTime messageTime;

}
