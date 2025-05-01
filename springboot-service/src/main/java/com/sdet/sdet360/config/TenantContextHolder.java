package com.sdet.sdet360.config;

import java.util.UUID;

/**
 * Holds the current tenant context for routing purposes.
 * Defaults to the MASTER_TENANT_ID if none is explicitly set.
 */
public class TenantContextHolder {

    /**
     * Identifier for the master (default) tenant.
     */
    public static final UUID MASTER_TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000009");

    // ThreadLocal to store tenant ID for each request thread.
    private static final ThreadLocal<UUID> currentTenant = ThreadLocal.withInitial(() -> MASTER_TENANT_ID);

    /**
     * Set the current tenant ID for this thread.
     * @param tenantId the tenant UUID to set
     */
    public static void setTenantId(UUID tenantId) {
        currentTenant.set(tenantId);
    }

    /**
     * Get the current tenant ID for this thread.
     * If none was set, returns MASTER_TENANT_ID.
     * @return the tenant UUID
     */
    public static UUID getTenantId() {
        UUID tenantId = currentTenant.get();
        return tenantId != null ? tenantId : MASTER_TENANT_ID;
    }

    /**
     * Clear the tenant ID from the current thread, resetting to default.
     */
    public static void clear() {
        currentTenant.remove();
    }
}
