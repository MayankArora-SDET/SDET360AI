package com.sdet.sdet360.tenant.auth;
import com.sdet.sdet360.config.TenantContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider tokenProvider;
    private final TenantAwareUserDetailsService userDetailsService;
    private final JwtCookieManager jwtCookieManager;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider,
                                 TenantAwareUserDetailsService userDetailsService,
                                 JwtCookieManager jwtCookieManager) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
        this.jwtCookieManager = jwtCookieManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Use the JwtCookieManager to get JWT from cookie or header
            String jwt = jwtCookieManager.getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // Get the username/email from the JWT token
                String usernameOrEmail = tokenProvider.getUsernameFromJWT(jwt);
                UUID tenantId = tokenProvider.getTenantIdFromJWT(jwt);

                // Update tenant context
                UUID currentTenantId = TenantContextHolder.getTenantId();
                if (currentTenantId == null || !currentTenantId.equals(tenantId)) {
                    logger.info("Setting tenant context to: {}", tenantId);
                    TenantContextHolder.setTenantId(tenantId);
                }

                // Load user details using either username or email
                UserDetails userDetails = userDetailsService.loadUserByUsername(usernameOrEmail);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }
}