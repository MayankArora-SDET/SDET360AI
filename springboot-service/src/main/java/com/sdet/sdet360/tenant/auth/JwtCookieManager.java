package com.sdet.sdet360.tenant.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtCookieManager {

    private static final String JWT_COOKIE_NAME = "session";

    @Value("${app.jwt.expiration}")
    private int jwtExpirationInMs;

    @Value("${app.jwt.cookie.secure:true}")
    private boolean secureCookie;

    @Value("${app.jwt.cookie.httpOnly:true}")
    private boolean httpOnlyCookie;

    @Value("${app.jwt.cookie.path:/}")
    private String cookiePath;

    /**
     * Add JWT token to response as a cookie
     */
    public void addTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, token);

        // Convert milliseconds to seconds as Cookie maxAge uses seconds
        cookie.setMaxAge(jwtExpirationInMs / 1000);
        cookie.setHttpOnly(httpOnlyCookie); // Prevents JavaScript access
        cookie.setSecure(secureCookie);     // Requires HTTPS
        cookie.setPath(cookiePath);

        response.addCookie(cookie);
    }

    /**
     * Remove JWT cookie when logging out
     */
    public void clearTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, "");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(false);
        cookie.setSecure(secureCookie);
        cookie.setPath(cookiePath);

        response.addCookie(cookie);
    }

    /**
     * Extract JWT token from cookie or Authorization header
     */
    public String getJwtFromRequest(HttpServletRequest request) {
        // First check in cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (JWT_COOKIE_NAME.equals(cookie.getName())) {
                    String tokenValue = cookie.getValue();
                    if (StringUtils.hasText(tokenValue)) {
                        return tokenValue;
                    }
                }
            }
        }

        // If not in cookies, fall back to Authorization header
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}