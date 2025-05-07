package com.sdet.sdet360.tenant.auth;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for JWT token generation and validation
 */
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {
    /**
     * Secret key used for signing JWT tokens
     */
    private String secret;

    /**
     * Token expiration time in seconds
     */
    private long expirationSeconds = 86400; // Default: 24 hours

    /**
     * Issuer claim for the JWT token
     */
    private String issuer = "token-service";
}
