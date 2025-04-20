package com.sdet.sdet360.master.repository;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sdet.sdet360.master.entity.Tenant;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Optional<Tenant> findByDomain(String domain);

	Optional<Tenant> findBySubdomain(String tenantSubdomain);
}
