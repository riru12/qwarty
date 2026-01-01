package com.qwarty.auth.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
public class GuestServiceTest {

    @Autowired
    private GuestService guestService;

    @MockitoSpyBean
    private JwtService jwtService;

    @Test
    void continueAsGuest_successful() {
        guestService.continueAsGuest();

        verify(jwtService, times(1)).generateGuestToken(any(UserDetails.class));
    }
}
