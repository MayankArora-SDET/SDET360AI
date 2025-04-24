// src/main/java/com/sdet/sdet360/config/TenantRoutingDataSourceConfig.java
package com.sdet.sdet360.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class TenantRoutingDataSourceConfig {

    @Bean
    public TenantAwareRoutingDataSource tenantAwareRoutingDataSource(
            @Qualifier("defaultDataSource") DataSource defaultDataSource) {

        TenantAwareRoutingDataSource routingDataSource = new TenantAwareRoutingDataSource();
        // set the default
        routingDataSource.setDefaultTargetDataSource(defaultDataSource);

        // **provide at least one entry so afterPropertiesSet()**
        Map<Object, Object> initialMap = new HashMap<>();
        initialMap.put(TenantContextHolder.MASTER_TENANT_ID.toString(), defaultDataSource);
        routingDataSource.setTargetDataSources(initialMap);

        // no explicit afterPropertiesSet() here—Spring will call it once
        return routingDataSource;
    }

    @Bean
    @Primary
    public DataSource dataSource(TenantAwareRoutingDataSource tenantAwareRoutingDataSource) {
        // drop the @DependsOn entirely
        return tenantAwareRoutingDataSource;
    }
}
