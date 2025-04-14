package com.sdet.sdet360.multitenant.config;

import com.sdet.sdet360.multitenant.model.Tenant;
import com.sdet.sdet360.multitenant.repository.TenantRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantResolverInterceptor implements HandlerInterceptor {

    private final TenantRepository tenantRepository;

    public TenantResolverInterceptor(TenantRepository tenantRepository) {
		this.tenantRepository = tenantRepository;
	}

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String domain = request.getServerName();
         
        
        Tenant tenant = tenantRepository.findByDomain(domain)
                .orElseThrow(() -> new IllegalArgumentException("Unknown tenant domain: " + domain));
        
        TenantContextHolder.setTenantId(tenant.getTenantId());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContextHolder.clear();
    }
}