package com.qwarty.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    private final String fakeSecretKey = "9IAo8iOsovTeKGxv8Qqs0GqzXZw9U7Ywr1lpxLThMlb";
    private final long expirationTime = 1000 * 60 * 60; // 1 hour

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // set @Value fields
        setField(jwtService, "secretKey", fakeSecretKey);
        setField(jwtService, "expirationTime", expirationTime);

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
    }

    // Utility method to set private fields
    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGenerateToken_noCustomClaims() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        String username = jwtService.extractClaim(token, Claims::getSubject);
        assertEquals("testuser", username);
    }

    @Test
    void testGenerateToken_withCustomClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        String token = jwtService.generateToken(userDetails, claims);

        assertNotNull(token);
        String username = jwtService.extractClaim(token, Claims::getSubject);
        String role = jwtService.extractClaim(token, c -> c.get("role", String.class));
        assertEquals("testuser", username);
        assertEquals("ADMIN", role);
    }

    @Test
    void testIsTokenValid_validToken() {
        String token = jwtService.generateToken(userDetails);
        boolean valid = jwtService.isTokenValid(token, userDetails);
        assertTrue(valid);
    }

    @Test
    void testIsTokenValid_expiredToken() {
        // Generate token with negative expiration to simulate expired token
        setField(jwtService, "expirationTime", -1);
        String token = jwtService.generateToken(userDetails, new HashMap<>());
        boolean valid = jwtService.isTokenValid(token, userDetails);
        assertFalse(valid);
    }

    @Test
    void testExtractClaim_customClaim() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("department", "engineering");
        String token = jwtService.generateToken(userDetails, claims);

        String dept = jwtService.extractClaim(token, c -> c.get("department", String.class));
        assertEquals("engineering", dept);
    }
}
