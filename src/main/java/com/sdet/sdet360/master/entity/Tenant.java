package com.sdet.sdet360.master.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data; 
import lombok.NoArgsConstructor; 

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "tenants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {
    @Id
    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "domain")
    private String domain;
    
    @Column(unique = true)
    private String subdomain;

    @Column(name = "db_url")
    private String dbUrl;

    @Column(name = "db_username")
    private String dbUsername;

    @Column(name = "db_password")
    private String dbPassword;

    @Column(name = "status", columnDefinition = "VARCHAR(50) DEFAULT 'active'")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        if (tenantId == null) {
            tenantId = UUID.randomUUID();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "active";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

	public UUID getTenantId() {
		return tenantId;
	}

	public void setTenantId(UUID tenantId) {
		this.tenantId = tenantId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getDbUsername() {
		return dbUsername;
	}

	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getSubdomain() {
		return subdomain;
	}

	public void setSubdomain(String subdomain) {
		this.subdomain = subdomain;
	}
	
}