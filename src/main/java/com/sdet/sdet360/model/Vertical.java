package com.sdet.sdet360.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "verticals")
public class Vertical extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "api_key")
    private String apiKey;
    
    @Column(name = "jira_username")
    private String jiraUsername;
    
    @Column(name = "jira_server_url")
    private String jiraServerUrl;
    
}
