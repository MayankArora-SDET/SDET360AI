package com.sdet.sdet360.tenant.model;


import jakarta.persistence.*;

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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getJiraUsername() {
		return jiraUsername;
	}

	public void setJiraUsername(String jiraUsername) {
		this.jiraUsername = jiraUsername;
	}

	public String getJiraServerUrl() {
		return jiraServerUrl;
	}

	public void setJiraServerUrl(String jiraServerUrl) {
		this.jiraServerUrl = jiraServerUrl;
	}
	

	public Vertical() {
	}

	public Vertical(User user, String name, String apiKey, String jiraUsername, String jiraServerUrl) {
		
		this.user = user;
		this.name = name;
		this.apiKey = apiKey;
		this.jiraUsername = jiraUsername;
		this.jiraServerUrl = jiraServerUrl;
	}
    
    
    
}
