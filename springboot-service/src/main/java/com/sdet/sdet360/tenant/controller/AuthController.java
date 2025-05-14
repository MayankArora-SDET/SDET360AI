package com.sdet.sdet360.tenant.controller;

import com.sdet.sdet360.config.TenantContextHolder;
import com.sdet.sdet360.tenant.auth.JwtCookieManager;
import com.sdet.sdet360.tenant.auth.JwtTokenProvider;
import com.sdet.sdet360.tenant.auth.TenantAwareUserDetails;
import com.sdet.sdet360.tenant.payload.JwtAuthenticationResponse;
import com.sdet.sdet360.tenant.payload.LoginRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CookieValue;
import java.util.Map;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private JwtCookieManager jwtCookieManager;

    @GetMapping("/check-session")
    public ResponseEntity<Map<String, Boolean>> checkSession(@CookieValue(value = "session", required = false) String sessionCookie) {
        boolean hasSession = sessionCookie != null && !sessionCookie.isEmpty();
        return ResponseEntity.ok(Map.of("session", hasSession));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                              HttpServletResponse response) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get current tenant ID
        UUID tenantId = TenantContextHolder.getTenantId();

        // Extract user ID from authentication object
        TenantAwareUserDetails userDetails = (TenantAwareUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        // Generate JWT token and set it as a cookie
        String jwt = tokenProvider.generateTokenAndSetCookie(authentication, tenantId, response);

        // Return the token in response body along with userId for API clients
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, userId));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        // Clear the JWT cookie
        jwtCookieManager.clearTokenCookie(response);

        return ResponseEntity.ok().body("You have been logged out successfully");
    }
}