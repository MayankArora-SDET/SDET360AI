// src/main/java/com/sdet/sdet360/config/TenantDataSourceInitializer.java
package com.sdet.sdet360.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.sdet.sdet360.master.repository.TenantRepository;
import com.zaxxer.hikari.HikariDataSource;

@Component
public class TenantDataSourceInitializer implements SmartInitializingSingleton {
    private static final Logger logger = LoggerFactory.getLogger(TenantDataSourceInitializer.class);

    private final TenantRepository tenantRepository;
    private final TenantAwareRoutingDataSource routingDataSource;

    public TenantDataSourceInitializer(TenantRepository tenantRepository,
                                       TenantAwareRoutingDataSource routingDataSource) {
        this.tenantRepository = tenantRepository;
        this.routingDataSource = routingDataSource;
    }

    @Override
    public void afterSingletonsInstantiated() {
        logger.info("Loading all tenant datasources now that JPA & Flyway are up");
        Map<Object, Object> targetDataSources = new HashMap<>();

        // load each tenant
        tenantRepository.findAll().forEach(tenant -> {
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(tenant.getDbUrl());
            ds.setUsername(tenant.getDbUsername());
            ds.setPassword(tenant.getDbPassword());
            ds.setMinimumIdle(2);
            ds.setMaximumPoolSize(10);

            String key = tenant.getTenantId().toString();
            targetDataSources.put(key, ds);
            logger.info("Registered tenant [{}] datasource", key);
        });

        // re‑add the master/default
        targetDataSources.put(
                TenantContextHolder.MASTER_TENANT_ID.toString(),
                routingDataSource.getResolvedDefaultDataSource()
        );

        // overwrite the routing map and re‑initialize
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.afterPropertiesSet();
        logger.info("Tenant routing datasource initialization complete");
    }
}
