package com.qwarty.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

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
        // lenient because malformed token test doesn't get to call this due to exception thrown
        lenient().when(userDetails.getUsername()).thenReturn("testuser");
        jwtService = new JwtService();

        // inject fake values using ReflectionTestUtils
        ReflectionTestUtils.setField(
                jwtService, "secretKey", "abcdefghijklmnopqrstuvwxzy1234567890abcdefghijklmnopqrstuvwxzy1234567890");
        ReflectionTestUtils.setField(jwtService, "accessExpirationTime", 3600000L);
        ReflectionTestUtils.setField(jwtService, "refreshExpirationTime", 7200000L);
    }

    @Test
    void testGenerateAccessToken() {
        String token = jwtService.generateAccessToken(userDetails);

        assertNotNull(token);
        String username = jwtService.extractSubject(token);
        String type = jwtService.extractType(token);

        assertEquals("testuser", username);
        assertEquals("ACCESS", type);
    }

    @Test
    void testGenerateRefreshToken() {
        String token = jwtService.generateRefreshToken(userDetails);

        assertNotNull(token);
        String username = jwtService.extractSubject(token);
        String type = jwtService.extractType(token);

        assertEquals("testuser", username);
        assertEquals("REFRESH", type);
    }

    @Test
    void testIsAccessTokenValid_validAccessToken() {
        String token = jwtService.generateAccessToken(userDetails);
        boolean valid = jwtService.isAccessTokenValid(token, userDetails);

        assertTrue(valid, "Access token should be valid for the user it was generated for");
    }

    @Test
    void testIsAccessTokenValid_refreshTokenShouldBeInvalid() {
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        boolean valid = jwtService.isAccessTokenValid(refreshToken, userDetails);

        assertFalse(valid, "Refresh token should not be valid when checking for access token");
    }

    @Test
    void testIsAccessTokenValid_expiredToken() {
        // generate expired token by using negative expiration
        ReflectionTestUtils.setField(jwtService, "accessExpirationTime", -1L);
        String token = jwtService.generateAccessToken(userDetails);

        // reset to normal expiration
        ReflectionTestUtils.setField(jwtService, "accessExpirationTime", 3600000L);

        boolean valid = jwtService.isAccessTokenValid(token, userDetails);
        assertFalse(valid, "Expired token should be invalid");
    }

    @Test
    void testIsAccessTokenValid_wrongUsername() {
        String token = jwtService.generateAccessToken(userDetails);

        UserDetails differentUser = org.mockito.Mockito.mock(UserDetails.class);
        when(differentUser.getUsername()).thenReturn("differentuser");

        boolean valid = jwtService.isAccessTokenValid(token, differentUser);
        assertFalse(valid, "Token should be invalid for a different user");
    }

    @Test
    void testIsAccessTokenValid_malformedToken() {
        boolean valid = jwtService.isAccessTokenValid("malformed.token.here", userDetails);
        assertFalse(valid, "Malformed token should be invalid");
    }

    @Test
    void testExtractClaim_subject() {
        String token = jwtService.generateAccessToken(userDetails);
        String username = jwtService.extractSubject(token);

        assertEquals("testuser", username);
    }

    @Test
    void testExtractClaim_tokenType() {
        String accessToken = jwtService.generateAccessToken(userDetails);
        String type = jwtService.extractType(accessToken);

        assertEquals("ACCESS", type);
    }
}
