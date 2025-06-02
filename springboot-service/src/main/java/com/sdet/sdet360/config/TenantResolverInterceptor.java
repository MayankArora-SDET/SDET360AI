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

    private static final String LOCALHOST_DOMAIN = "localhost";
    private static final String PRIMARY_DOMAIN = "sdet360.ai";
    private static final String SECONDARY_DOMAIN = "sdet360";
    private static final String RUNPOD_DOMAIN = "103.196.86.35";
    private static final String RUNPOD_DOMAIN_URL = "3zeb3azkqo7l80-8080.proxy.runpod.net";

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

        // Handle master tenant API routes
        if (requestURI.startsWith("/api/tenants")) {
            logger.info("Master tenant access detected, setting MASTER_TENANT_ID.");
            TenantContextHolder.setTenantId(MASTER_TENANT_ID);
            return true;
        }

        // Try to resolve tenant from header first
        if (tenantIdHeader != null && !tenantIdHeader.isEmpty()) {
            if (resolveTenantFromHeader(tenantIdHeader)) {
                return true;
            }
        }

        // Try to resolve tenant from domain/subdomain
        Optional<UUID> tenantId = resolveTenantFromDomainAndSubdomain(request);

        if (tenantId.isPresent()) {
            logger.info("Tenant resolved from domain/subdomain: {}", tenantId.get());
            TenantContextHolder.setTenantId(tenantId.get());
            return true;
        }

        // Fallback logic for specific domains
        handleFallbackTenantResolution(serverName);
        return true;
    }

    private boolean resolveTenantFromHeader(String tenantIdHeader) {
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
        return false;
    }

    private Optional<UUID> resolveTenantFromDomainAndSubdomain(HttpServletRequest request) {
        String serverName = request.getServerName();
        logger.info("Resolving tenant from server name: {}", serverName);

        UUID originalTenantId = TenantContextHolder.getTenantId();
        TenantContextHolder.setTenantId(MASTER_TENANT_ID);

        try {
            DomainInfo domainInfo = parseDomainInfo(serverName);
            logger.debug("Parsed domain info - Domain: {}, Subdomain: {}", domainInfo.domain, domainInfo.subdomain);

            if (domainInfo.subdomain != null) {
                Optional<Tenant> tenant = tenantRepository.findByDomainAndSubdomain(domainInfo.domain, domainInfo.subdomain);
                if (tenant.isPresent()) {
                    logger.info("Tenant found by domain '{}' and subdomain '{}': {}",
                            domainInfo.domain, domainInfo.subdomain, tenant.get().getTenantId());
                    return Optional.of(tenant.get().getTenantId());
                }
            }

            Optional<Tenant> tenant = tenantRepository.findByDomain(domainInfo.domain);
            if (tenant.isPresent()) {
                logger.info("Tenant found by domain '{}': {}", domainInfo.domain, tenant.get().getTenantId());
                return Optional.of(tenant.get().getTenantId());
            }

            if (LOCALHOST_DOMAIN.equals(domainInfo.domain) && domainInfo.subdomain != null) {
                tenant = tenantRepository.findBySubdomain(domainInfo.subdomain);
                if (tenant.isPresent()) {
                    logger.info("Tenant found by subdomain '{}' on localhost: {}",
                            domainInfo.subdomain, tenant.get().getTenantId());
                    return Optional.of(tenant.get().getTenantId());
                }
            }

            logger.warn("No tenant found for domain '{}' and subdomain '{}'", domainInfo.domain, domainInfo.subdomain);
            return Optional.empty();

        } finally {
            TenantContextHolder.setTenantId(originalTenantId);
        }
    }

    private DomainInfo parseDomainInfo(String serverName) {
        if (serverName == null || serverName.isEmpty()) {
            return new DomainInfo(null, null);
        }

        if (serverName.endsWith("." + LOCALHOST_DOMAIN)) {
            String subdomain = serverName.replace("." + LOCALHOST_DOMAIN, "");
            return new DomainInfo(LOCALHOST_DOMAIN, subdomain.isEmpty() ? null : subdomain);
        }
        if (serverName.endsWith("." + RUNPOD_DOMAIN)) {
            String subdomain = serverName.replace("." + RUNPOD_DOMAIN, "");
            if (!subdomain.isEmpty()) {
                return new DomainInfo(RUNPOD_DOMAIN, subdomain);
            }
        }
        if (serverName.endsWith("." + RUNPOD_DOMAIN_URL)) {
            String subdomain = serverName.replace("." + RUNPOD_DOMAIN_URL, "");
            if (!subdomain.isEmpty()) {
                return new DomainInfo(RUNPOD_DOMAIN_URL, subdomain);
            }
        }

        String[] parts = serverName.split("\\.");

        if (parts.length >= 3) {
            String subdomain = parts[0];
            String domain = String.join(".", java.util.Arrays.copyOfRange(parts, 1, parts.length));
            return new DomainInfo(domain, subdomain);
        } else if (parts.length == 2) {
            return new DomainInfo(serverName, null);
        } else {
            return new DomainInfo(serverName, null);
        }
    }

    private void handleFallbackTenantResolution(String serverName) {
        if (LOCALHOST_DOMAIN.equals(serverName) || PRIMARY_DOMAIN.equals(serverName) || SECONDARY_DOMAIN.equals(serverName)||RUNPOD_DOMAIN.equals(serverName)||RUNPOD_DOMAIN_URL.equals(serverName)) {
            Optional<Tenant> firstTenant = tenantRepository.findAll().stream().findFirst();
            if (firstTenant.isPresent()) {
                logger.warn("No specific tenant matched for '{}', defaulting to first tenant: {}",
                        serverName, firstTenant.get().getTenantId());
                TenantContextHolder.setTenantId(firstTenant.get().getTenantId());
            } else {
                logger.error("No tenants found in system for fallback.");
                throw new IllegalStateException("No tenants found in the system. Please create at least one tenant.");
            }
        } else {
            logger.error("Unknown tenant domain: {}", serverName);
            throw new IllegalArgumentException("Unknown tenant domain: " + serverName);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContextHolder.clear();
        logger.debug("Cleared tenant context after request completion.");
    }

    private static class DomainInfo {
        final String domain;
        final String subdomain;

        DomainInfo(String domain, String subdomain) {
            this.domain = domain;
            this.subdomain = subdomain;
        }
    }
}