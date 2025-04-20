package com.sdet.sdet360.tenant.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sdet.sdet360.tenant.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	List<User> findByEmail(String email);

	List<User> findByUsername(String username); 
}
