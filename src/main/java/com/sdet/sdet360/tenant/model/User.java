package com.sdet.sdet360.tenant.model;


import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
public class User extends BaseEntity {
    
    @Column(name = "username")
    private String username;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "password_hash")
    private String passwordHash;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	

	public User() {
	}

	public User(String username, String email, String passwordHash) {
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
	}

    
}
