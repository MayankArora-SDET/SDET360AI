package com.sdet.sdet360.tenant.controller;

import com.sdet.sdet360.config.TenantContextHolder;
import com.sdet.sdet360.tenant.auth.*;
import com.sdet.sdet360.tenant.payload.JwtAuthenticationResponse;
import com.sdet.sdet360.tenant.payload.LoginRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.UUID;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) { 
        UUID tenantId = TenantContextHolder.getTenantId();
        
        logger.info("Login attempt for user: {} in tenant: {}", loginRequest.getUsername(), tenantId);
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication, tenantId);
        logger.info("Successfully authenticated user: {}", loginRequest.getUsername());
        
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }
}
