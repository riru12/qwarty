package com.qwarty.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.qwarty.auth.lov.JwtTokenType;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    private final String testUsername = "testuser";

    @BeforeEach
    void setUp() {
        // lenient because malformed token test doesn't get to call this due to exception thrown
        lenient().when(userDetails.getUsername()).thenReturn(testUsername);
    }

    @Test
    void testGenerateAccessToken() {
        String token = jwtService.generateAccessToken(userDetails);

        assertNotNull(token);
        String username = jwtService.extractSubject(token);
        JwtTokenType type = jwtService.extractType(token);

        assertEquals(testUsername, username);
        assertEquals(JwtTokenType.ACCESS, type);
    }

    @Test
    void testGenerateRefreshToken() {
        String token = jwtService.generateRefreshToken(userDetails);

        assertNotNull(token);
        String username = jwtService.extractSubject(token);
        JwtTokenType type = jwtService.extractType(token);

        assertEquals(testUsername, username);
        assertEquals(JwtTokenType.REFRESH, type);
    }

    @Test
    void testIsAccessTokenValid_validAccessToken() {
        String token = jwtService.generateAccessToken(userDetails);
        boolean valid = jwtService.isUserAccessTokenValid(token, userDetails);

        assertTrue(valid, "Access token should be valid for the user it was generated for");
    }

    @Test
    void testIsAccessTokenValid_refreshTokenShouldBeInvalid() {
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        boolean valid = jwtService.isUserAccessTokenValid(refreshToken, userDetails);

        assertFalse(valid, "Refresh token should not be valid when checking for access token");
    }

    @Test
    void testIsAccessTokenValid_expiredToken() {
        try {
            // get original value of accessExpirationTime
            Field accessField = JwtService.class.getDeclaredField("accessExpirationTime");
            accessField.setAccessible(true);
            Long originalExpiration = (Long) accessField.get(jwtService);

            // temporarily set negative expiration to generate expired token
            accessField.set(jwtService, -1L);
            String token = jwtService.generateAccessToken(userDetails);

            // restore original expiration injected by Spring
            accessField.set(jwtService, originalExpiration);

            boolean valid = jwtService.isUserAccessTokenValid(token, userDetails);
            assertFalse(valid, "Expired token should be invalid");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to access or modify accessExpirationTime: " + e.getMessage());
        }
    }

    @Test
    void testIsAccessTokenValid_wrongUsername() {
        String token = jwtService.generateAccessToken(userDetails);

        UserDetails differentUser = org.mockito.Mockito.mock(UserDetails.class);
        when(differentUser.getUsername()).thenReturn("differentuser");

        boolean valid = jwtService.isUserAccessTokenValid(token, differentUser);
        assertFalse(valid, "Token should be invalid for a different user");
    }

    @Test
    void testIsAccessTokenValid_malformedToken() {
        boolean valid = jwtService.isUserAccessTokenValid("malformed.token.here", userDetails);
        assertFalse(valid, "Malformed token should be invalid");
    }

    @Test
    void testExtractClaim_subject() {
        String token = jwtService.generateAccessToken(userDetails);
        String username = jwtService.extractSubject(token);

        assertEquals(testUsername, username);
    }

    @Test
    void testExtractClaim_tokenType() {
        String accessToken = jwtService.generateAccessToken(userDetails);
        JwtTokenType type = jwtService.extractType(accessToken);

        assertEquals(JwtTokenType.ACCESS, type);
    }
}
