package com.qwarty.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Claims;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        when(userDetails.getUsername()).thenReturn("testuser");

        jwtService = new JwtService();

        // inject fake values using ReflectionTestUtils
        ReflectionTestUtils.setField(jwtService, "secretKey", "abcdefghijklmnopqrstuvwxzy1234567890abcdefghijklmnopqrstuvwxzy1234567890");
        ReflectionTestUtils.setField(jwtService, "expirationTime", 3600000L);
    }

    // utility method to set private fields
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
        assertTrue(valid, "Token should be valid for the user it was generated for");
    }

    @Test
    void testIsTokenValid_expiredToken() {
        // generate expired token by using negative expiration
        setField(jwtService, "expirationTime", -1L);
        String token = jwtService.generateToken(userDetails, new HashMap<>());

        // reset to normal expiration
        setField(jwtService, "expirationTime", 3600000L);
        boolean valid = jwtService.isTokenValid(token, userDetails);
        assertFalse(valid, "Expired token should be invalid");
    }

    @Test
    void testExtractClaim_customClaim() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rank", "master typist");
        String token = jwtService.generateToken(userDetails, claims);
        String rank = jwtService.extractClaim(token, c -> c.get("rank", String.class));
        assertEquals("master typist", rank);
    }
}
