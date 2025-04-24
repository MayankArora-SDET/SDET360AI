package com.sdet.sdet360.tenant.repository;

import com.sdet.sdet360.tenant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	List<User> findByEmail(String email);

	List<User> findByUsername(String username); 
}
