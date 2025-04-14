package com.sdet.sdet360.multitenant.config;

import java.util.UUID;

import org.springframework.context.annotation.Configuration;

@Configuration
public class TenantContextHolder {
    private static final ThreadLocal<UUID> currentTenant = new ThreadLocal<>();
    
    public static void setTenantId(UUID tenantId) {
        currentTenant.set(tenantId);
    }
    
    public static UUID getTenantId() {
        return currentTenant.get();
    }
    
    public static void clear() {
        currentTenant.remove();
    }
}