package com.sdet.sdet360.multitenant.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final TenantResolverInterceptor tenantResolverInterceptor;
    

    public WebConfiguration(TenantResolverInterceptor tenantResolverInterceptor) {
		this.tenantResolverInterceptor = tenantResolverInterceptor;
	}


	@Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantResolverInterceptor);
    }
}
