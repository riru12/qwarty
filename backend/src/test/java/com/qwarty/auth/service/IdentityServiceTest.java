package com.qwarty.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.qwarty.auth.dto.IdentityResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class IdentityServiceTest {

    @Autowired
    private IdentityService identityService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void me_ShouldReturnIdentityResponse_WhenTokenIsValidUser() {
        String accessToken = "user-access-token";

        when(jwtService.extractSubject(accessToken)).thenReturn("brill");
        when(jwtService.isGuestToken(accessToken)).thenReturn(false);

        IdentityResponseDTO result = identityService.me(accessToken);

        assertEquals("brill", result.username());
        assertEquals(false, result.isGuest());

        verify(jwtService, times(1)).extractSubject(accessToken);
        verify(jwtService, times(1)).isGuestToken(accessToken);
    }

    @Test
    void me_ShouldReturnGuestIdentity_WhenTokenIsGuest() {
        String accessToken = "guest-access-token";

        when(jwtService.extractSubject(accessToken)).thenReturn("guest");
        when(jwtService.isGuestToken(accessToken)).thenReturn(true);

        IdentityResponseDTO result = identityService.me(accessToken);

        assertEquals("guest", result.username());
        assertEquals(true, result.isGuest());

        verify(jwtService, times(1)).extractSubject(accessToken);
        verify(jwtService, times(1)).isGuestToken(accessToken);
    }
}
