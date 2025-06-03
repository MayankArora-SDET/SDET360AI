package com.sdet.sdet360.master.service;

import com.sdet.sdet360.config.TenantAwareRoutingDataSource;
import com.sdet.sdet360.master.entity.Tenant;
import com.sdet.sdet360.master.repository.TenantRepository;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class TenantService {

    private static final Logger logger = LoggerFactory.getLogger(TenantService.class);

    private final TenantRepository tenantRepository;
    private final String adminDbUrl;
    private final String adminUsername;
    private final String adminPassword;
    
    @Autowired
    private TenantAwareRoutingDataSource tenantAwareRoutingDataSource;

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
            tenant.setDbUrl(tenant.getDbUrl().isEmpty() ? "jdbc:postgresql://localhost:5432/" + dbName : tenant.getDbUrl());
            tenant.setDbUsername(adminUsername);
            tenant.setDbPassword(adminPassword);

            // Save the tenant first
            Tenant saved = tenantRepository.save(tenant);
            
            // Run migrations
            runFlywayMigration(saved);
            
            // Register the new tenant's data source
            registerTenantDataSource(saved);
            
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
    
    /**
     * Registers a new tenant's data source in the routing data source
     * @param tenant The tenant whose data source needs to be registered
     */
    private void registerTenantDataSource(Tenant tenant) {
        try {
            // Create a new data source for the tenant
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(tenant.getDbUrl());
            dataSource.setUsername(tenant.getDbUsername());
            dataSource.setPassword(tenant.getDbPassword());
            dataSource.setMinimumIdle(2);
            dataSource.setMaximumPoolSize(10);
            dataSource.setDriverClassName("org.postgresql.Driver");

            // Get the current target data sources
            Map<Object, Object> targetDataSources = new HashMap<>(
                tenantAwareRoutingDataSource.getResolvedDataSources()
            );
            
            // Add the new tenant's data source
            targetDataSources.put(tenant.getTenantId().toString(), dataSource);
            
            // Update the routing data source
            tenantAwareRoutingDataSource.setTargetDataSources(targetDataSources);
            tenantAwareRoutingDataSource.afterPropertiesSet();
            
            logger.info("Registered new tenant data source for tenant: {}", tenant.getTenantId());
        } catch (Exception e) {
            logger.error("Failed to register tenant data source for tenant: " + tenant.getTenantId(), e);
            throw new RuntimeException("Failed to register tenant data source", e);
        }
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