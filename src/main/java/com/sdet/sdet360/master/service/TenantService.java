package com.sdet.sdet360.master.service;

import com.sdet.sdet360.master.entity.Tenant;
import com.sdet.sdet360.master.repository.TenantRepository;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;
    private final String adminDbUrl;
    private final String adminUsername;
    private final String adminPassword;

    /**
     * Single constructor — Spring will wire up:
     *  1) your JPA TenantRepository
     *  2) the default‐datasource creds from application.properties
     */
    public TenantService(
            TenantRepository tenantRepository,
            @Value("${spring.datasource.url}") String adminDbUrl,
            @Value("${spring.datasource.username}") String adminUsername,
            @Value("${spring.datasource.password}") String adminPassword
    ) {
        this.tenantRepository = tenantRepository;
        this.adminDbUrl      = adminDbUrl;
        this.adminUsername   = adminUsername;
        this.adminPassword   = adminPassword;
    }

    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    public Optional<Tenant> getTenantById(UUID id) {
        return tenantRepository.findById(id);
    }

    @Transactional
    public Tenant createTenant(Tenant tenant) {
        if (tenant.getTenantId() == null) {
            tenant.setTenantId(UUID.randomUUID());
        }
        LocalDateTime now = LocalDateTime.now();
        tenant.setCreatedAt(now);
        tenant.setUpdatedAt(now);
        if (tenant.getStatus() == null) {
            tenant.setStatus("active");
        }
        if (tenant.getSubdomain() == null || tenant.getSubdomain().isEmpty()) {
            tenant.setSubdomain(tenant.getName()
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]", ""));
        }
        if (tenant.getDomain() == null || tenant.getDomain().isEmpty()) {
            tenant.setDomain(tenant.getSubdomain() + ".oa.com");
        }

        String dbName = "tenant_" +
                tenant.getName().toLowerCase().replaceAll("[^a-z0-9]", "_");

        try {
            createTenantDatabase(dbName);
            tenant.setDbUrl("jdbc:postgresql://localhost:5432/" + dbName);
            tenant.setDbUsername(adminUsername);
            tenant.setDbPassword(adminPassword);

            Tenant saved = tenantRepository.save(tenant);
            runFlywayMigration(saved);
            return saved;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create tenant", e);
        }
    }

    private void createTenantDatabase(String dbName) throws SQLException {
        try (Connection conn = DriverManager.getConnection(
                adminDbUrl, adminUsername, adminPassword);
             var stmt = conn.createStatement()
        ) {
            // if it doesn’t exist, create it
            stmt.execute("SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'");
            try (var rs = stmt.getResultSet()) {
                if (!rs.next()) {
                    stmt.execute("CREATE DATABASE " + dbName);
                }
            }
        }
    }

    public void runFlywayMigration(Tenant tenant) {
        Flyway.configure()
                .dataSource(
                        tenant.getDbUrl(),
                        tenant.getDbUsername(),
                        tenant.getDbPassword()
                )
                .locations("classpath:db/tenant_migrations")
                .load()
                .migrate();
    }

    @Transactional
    public Tenant updateTenant(UUID id, Tenant details) {
        return tenantRepository.findById(id)
                .map(existing -> {
                    existing.setName(details.getName());
                    existing.setDomain(details.getDomain());
                    existing.setStatus(details.getStatus());
                    existing.setUpdatedAt(LocalDateTime.now());
                    return tenantRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Tenant not found: " + id));
    }

    @Transactional
    public void deleteTenant(UUID id) {
        Tenant t = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found: " + id));
        t.setStatus("inactive");
        tenantRepository.save(t);
    }
}
