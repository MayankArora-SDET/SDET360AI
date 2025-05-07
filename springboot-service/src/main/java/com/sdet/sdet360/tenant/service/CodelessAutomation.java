package com.sdet.sdet360.tenant.service;


import com.sdet.sdet360.tenant.auth.JwtProperties;
import com.sdet.sdet360.tenant.dto.TokenDataDto;
import com.sdet.sdet360.tenant.dto.TokenRequestDto;
import com.sdet.sdet360.tenant.exception.TokenGenerationException;
import com.sdet.sdet360.tenant.exception.TokenValidationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class  CodelessAutomation{

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public CodelessAutomation(JwtProperties jwtProperties, SecretKey jwtSecretKey) {
        this.jwtProperties = jwtProperties;
        this.secretKey = jwtSecretKey;
    }

    /**
     * Generates a token based on the provided request and authenticated user
     *
     * @param request TokenRequestDto containing request parameters
     * @return Generated JWT token string
     * @throws TokenGenerationException if token generation fails
     */
    public String generateToken(TokenRequestDto request) throws TokenGenerationException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                throw new TokenGenerationException("No authenticated user found");
            }

            String username = authentication.getName();
            String vertical = extractVerticalFromAuthentication(authentication);

            Instant now = Instant.now();
            Instant expiration = now.plusSeconds(jwtProperties.getExpirationSeconds());

            Map<String, Object> claims = new HashMap<>();
            claims.put("username", username);
            claims.put("url", request.getUrl());
            claims.put("description", request.getDescription());
            claims.put("testCaseId", request.getTestCaseId());
            claims.put("vertical", vertical);
            claims.put("category", request.getCategory());

            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuer(jwtProperties.getIssuer())
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(expiration))
                    .signWith(secretKey)
                    .compact();
        } catch (Exception e) {
            throw new TokenGenerationException("Failed to generate token: " + e.getMessage(), e);
        }
    }

    /**
     * Validates a token and returns its data
     *
     * @param token JWT token string
     * @return TokenDataDto containing token data
     * @throws TokenValidationException if token is invalid or expired
     */
    public TokenDataDto validateToken(String token) throws TokenValidationException {
        try {
            // Parse and validate token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Extract token data
            return TokenDataDto.builder()
                    .username((String) claims.get("username"))
                    .url((String) claims.get("url"))
                    .description((String) claims.get("description"))
                    .testCaseId((String) claims.get("testCaseId"))
                    .vertical((String) claims.get("vertical"))
                    .category((String) claims.get("category"))
                    .issuedAt(toLocalDateTime(claims.getIssuedAt()))
                    .expiresAt(toLocalDateTime(claims.getExpiration()))
                    .build();
        } catch (ExpiredJwtException e) {
            throw new TokenValidationException("Token has expired", e);
        } catch (SignatureException e) {
            throw new TokenValidationException("Invalid token signature", e);
        } catch (MalformedJwtException e) {
            throw new TokenValidationException("Malformed token", e);
        } catch (Exception e) {
            throw new TokenValidationException("Token validation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts the vertical information from the authentication object
     *
     * @param authentication Current authentication object
     * @return Vertical value
     */
    private String extractVerticalFromAuthentication(Authentication authentication) {
        // This is a placeholder - implement according to your authentication setup
        // Options:
        // 1. Extract from JWT claims if using a JWT filter
        // 2. Extract from UserDetails if stored there
        // 3. Extract from a custom Authentication implementation

        // Example implementation - adjust based on your actual auth setup
        return "default_vertical";
    }

    /**
     * Converts Date to LocalDateTime
     */
    private LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * Get raw claims from token without validation
     * Useful for debugging or extracting data from expired tokens
     */
    public Map<String, Object> extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // For expired tokens, still return the claims
            return e.getClaims();
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}