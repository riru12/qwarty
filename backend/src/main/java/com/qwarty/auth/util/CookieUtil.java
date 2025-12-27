package com.qwarty.auth.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${environment}")
    private String environment;

    /**
     * Sets accessToken cookie in the HTTP headers
     */
    public void setAccessCookie(String accessToken, Instant accessTokenExpiry, HttpServletResponse response) {
        boolean isProd = "PROD".equals(environment);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(isProd ? true : false)
                .sameSite(isProd ? "Strict" : "Lax")
                .path("/api")
                .maxAge(Duration.between(Instant.now(), accessTokenExpiry))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    }

    /**
     * Sets refreshToken cookie in the HTTP headers
     */
    public void setRefreshCookie(String refreshToken, Instant refreshTokenExpiry, HttpServletResponse response) {
        boolean isProd = "PROD".equals(environment);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(isProd ? true : false)
                .sameSite(isProd ? "Strict" : "Lax")
                .path("/api/auth/session")
                .maxAge(Duration.between(Instant.now(), refreshTokenExpiry))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    /**
     * Clears accessToken cookie in the HTTP headers
     */
    public void clearAccessCookie(HttpServletResponse response) {
        boolean isProd = "PROD".equals(environment);

        ResponseCookie refreshCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(isProd ? true : false)
                .sameSite(isProd ? "Strict" : "Lax")
                .path("/api")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    /**
     * Clears refreshToken cookie in the HTTP headers
     */
    public void clearRefreshCookie(HttpServletResponse response) {
        boolean isProd = "PROD".equals(environment);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(isProd ? true : false)
                .sameSite(isProd ? "Strict" : "Lax")
                .path("/api/auth/session")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    /**
     * Retrieves accessToken from HTTP headers
     */
    public String getAccessTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (var cookie : request.getCookies()) {
            if ("accessToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
