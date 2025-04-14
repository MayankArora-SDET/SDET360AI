package com.sdet.sdet360.multitenant.config;


import com.sdet.sdet360.multitenant.model.Tenant;
import com.sdet.sdet360.multitenant.repository.TenantRepository;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
@Configuration
public class DataSourceConfiguration {

    private final TenantContextHolder tenantContextHolder;
    
    // Remove the direct dependency on TenantRepository
    public DataSourceConfiguration(TenantContextHolder tenantContextHolder) {
        this.tenantContextHolder = tenantContextHolder;
    }
    
    @Bean
    @Primary
    public DataSource dataSource() {
        TenantAwareRoutingDataSource dataSource = new TenantAwareRoutingDataSource();
        // Set a default data source initially
        dataSource.setDefaultTargetDataSource(createDefaultDataSource());
        // Empty map initially
        dataSource.setTargetDataSources(new HashMap<>());
        return dataSource;
    }
    
    // Add a method to create a default data source
    private DataSource createDefaultDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        // Use properties from application.properties
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/mastersdet360");
        dataSource.setUsername("postgres");
        dataSource.setPassword("12345");
        dataSource.setMinimumIdle(2);
        dataSource.setMaximumPoolSize(10);
        return dataSource;
    }
    
    // Use a post-construct or application ready event to load tenants
    @Bean
    public TenantDataSourceInitializer tenantDataSourceInitializer(
            TenantRepository tenantRepository, 
            TenantAwareRoutingDataSource dataSource) {
        return new TenantDataSourceInitializer(tenantRepository, dataSource);
    }
    
    // This class will initialize tenant data sources after the main configuration is done
    @Component
    public static class TenantDataSourceInitializer {
        private final TenantRepository tenantRepository;
        private final TenantAwareRoutingDataSource dataSource;
        
        public TenantDataSourceInitializer(TenantRepository tenantRepository, 
                                          TenantAwareRoutingDataSource dataSource) {
            this.tenantRepository = tenantRepository;
            this.dataSource = dataSource;
        }
        
        @PostConstruct
        public void loadTenants() {
            Map<Object, Object> tenantDataSources = new HashMap<>();
            tenantRepository.findAll().forEach(tenant -> {
                DataSource tenantDataSource = createTenantDataSource(tenant);
                tenantDataSources.put(tenant.getTenantId(), tenantDataSource);
            });
            dataSource.setTargetDataSources(tenantDataSources);
            dataSource.afterPropertiesSet(); // Important to call this to refresh lookup map
        }
        
        private DataSource createTenantDataSource(Tenant tenant) {
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(tenant.getDbUrl());
            dataSource.setUsername(tenant.getDbUsername());
            dataSource.setPassword(tenant.getDbPassword());
            dataSource.setMinimumIdle(2);
            dataSource.setMaximumPoolSize(10);
            return dataSource;
        }
    }
    
    // Make your routing data source a public class so it can be autowired
    public static class TenantAwareRoutingDataSource extends AbstractRoutingDataSource {
        @Override
        protected Object determineCurrentLookupKey() {
            return TenantContextHolder.getTenantId();
        }
    }
}