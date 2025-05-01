package com.sdet.sdet360.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class TenantAwareRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContextHolder.getTenantId() != null
                ? TenantContextHolder.getTenantId().toString()
                : null;
    }
}
