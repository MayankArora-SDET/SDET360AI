package com.sdet.sdet360.config;

import com.sdet.sdet360.master.entity.Tenant;
import com.sdet.sdet360.master.repository.TenantRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;
import java.util.UUID;

@Component
public class TenantResolverInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TenantResolverInterceptor.class);

    private final TenantRepository tenantRepository;
    private final UUID MASTER_TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000009");

    public TenantResolverInterceptor(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String serverName = request.getServerName();
        String tenantIdHeader = request.getHeader("X-Tenant-ID");

        logger.info("Incoming request: [{}] {} from domain: {} with tenant header: {}", method, requestURI, serverName, tenantIdHeader);

        if (requestURI.startsWith("/api/tenants")) {
            logger.info("Master tenant access detected, setting MASTER_TENANT_ID.");
            TenantContextHolder.setTenantId(MASTER_TENANT_ID);
            return true;
        }

        if (tenantIdHeader != null && !tenantIdHeader.isEmpty()) {
            try {
                UUID tenantId = UUID.fromString(tenantIdHeader);
                if (tenantRepository.existsById(tenantId)) {
                    logger.info("Tenant resolved from header: {}", tenantId);
                    TenantContextHolder.setTenantId(tenantId);
                    return true;
                } else {
                    logger.warn("Tenant ID from header does not exist: {}", tenantId);
                }
            } catch (IllegalArgumentException e) {
                logger.error("Invalid tenant ID format in header: {}", tenantIdHeader, e);
            }
        }

        Optional<UUID> tenantId = resolveTenantFromDomain(request);

        if (tenantId.isPresent()) {
            logger.info("Tenant resolved from domain: {}", tenantId.get());
            TenantContextHolder.setTenantId(tenantId.get());
        } else {
            if ("localhost".equals(serverName)) {
                Optional<Tenant> firstTenant = tenantRepository.findAll().stream().findFirst();
                if (firstTenant.isPresent()) {
                    logger.warn("No domain matched, defaulting to first tenant: {}", firstTenant.get().getTenantId());
                    TenantContextHolder.setTenantId(firstTenant.get().getTenantId());
                } else {
                    logger.error("No tenants found in system.");
                    throw new IllegalStateException("No tenants found in the system. Please create at least one tenant.");
                }
            } else {
                logger.error("Unknown tenant domain: {}", serverName);
                throw new IllegalArgumentException("Unknown tenant domain: " + serverName);
            }
        }

        return true;
    }

    private Optional<UUID> resolveTenantFromDomain(HttpServletRequest request) {
        String domain = request.getServerName();
        logger.info("Resolving tenant from domain: {}", domain);

        // Temporarily set to master tenant to ensure we can access the tenants table
        UUID originalTenantId = TenantContextHolder.getTenantId();
        TenantContextHolder.setTenantId(MASTER_TENANT_ID);

        try {
            if (domain.endsWith(".localhost")) {
                String tenantSubdomain = domain.replace(".localhost", "");
                logger.debug("Detected subdomain: {}", tenantSubdomain);
                return tenantRepository.findBySubdomain(tenantSubdomain)
                        .map(Tenant::getTenantId);
            }

            return tenantRepository.findByDomain(domain)
                    .map(Tenant::getTenantId);
        } finally {
            TenantContextHolder.setTenantId(originalTenantId);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContextHolder.clear();
        logger.debug("Cleared tenant context after request completion.");
    }
}
