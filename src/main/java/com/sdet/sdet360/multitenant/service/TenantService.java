package com.sdet.sdet360.multitenant.service;


import com.sdet.sdet360.multitenant.model.Tenant;
import com.sdet.sdet360.multitenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
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
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final String adminDbUrl;
    private final String adminUsername;
    private final String adminPassword;
    
    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
        this.adminDbUrl = "jdbc:postgresql://localhost:5432/mastersdet360";
        this.adminUsername = "postgres";
        this.adminPassword = "12345";
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
        
        String dbName = "tenant_" + tenant.getName().toLowerCase().replaceAll("[^a-z0-9]", "_");

        try {
            createTenantDatabase(dbName);
            tenant.setDbUrl("jdbc:postgresql://localhost:5432/" + dbName);
            tenant.setDbUsername("tenant_user"); 
            tenant.setDbPassword(generateSecurePassword());
            
            Tenant savedTenant = tenantRepository.save(tenant);
            
            runFlywayMigration(savedTenant);
            
            return savedTenant;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create tenant: " + e.getMessage(), e);
        }
    }
    
    private void createTenantDatabase(String dbName) throws SQLException {
        try (Connection connection = DriverManager.getConnection(adminDbUrl, adminUsername, adminPassword)) {
            try (var stmt = connection.createStatement()) {
                stmt.execute("SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'");
                try (var rs = stmt.getResultSet()) {
                    if (!rs.next()) {
                        stmt.execute("CREATE DATABASE " + dbName);
                    }
                }
            }
        }
    }
    
    private String generateSecurePassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    public void runFlywayMigration(Tenant tenant) {
        Flyway flyway = Flyway.configure()
                .dataSource(tenant.getDbUrl(), tenant.getDbUsername(), tenant.getDbPassword())
                .locations("classpath:db/migration/V01__tenants")
                .load();
        
        flyway.migrate();
    }
    
    @Transactional
    public Tenant updateTenant(UUID id, Tenant tenantDetails) {
        return tenantRepository.findById(id)
                .map(existingTenant -> {
                    existingTenant.setName(tenantDetails.getName());
                    existingTenant.setDomain(tenantDetails.getDomain());
                    existingTenant.setStatus(tenantDetails.getStatus());
                    existingTenant.setAdditionalSettings(tenantDetails.getAdditionalSettings());
                    existingTenant.setUpdatedAt(LocalDateTime.now());
                    return tenantRepository.save(existingTenant);
                })
                .orElseThrow(() -> new RuntimeException("Tenant not found with id: " + id));
    }
    
    @Transactional
    public void deleteTenant(UUID id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found with id: " + id));
                tenant.setStatus("inactive");
        tenantRepository.save(tenant);
    }
}
