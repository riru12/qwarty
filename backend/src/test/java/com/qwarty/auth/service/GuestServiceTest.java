package com.qwarty.auth.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.qwarty.auth.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
public class GuestServiceTest {

    @Autowired
    private GuestService guestService;

    @MockitoSpyBean
    private JwtService jwtService;

    @MockitoBean
    private CookieUtil cookieUtil;

    @Test
    void continueAsGuest_shouldGenerateGuestTokenAndSetCookie() {
        HttpServletResponse response = org.mockito.Mockito.mock(HttpServletResponse.class);

        guestService.continueAsGuest(response);

        verify(jwtService, times(1)).generateGuestToken(any(UserDetails.class));
        verify(cookieUtil, times(1)).setAccessCookie(any(String.class), any(), any(HttpServletResponse.class));
    }
}
