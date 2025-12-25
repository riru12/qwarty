package com.qwarty.auth.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.qwarty.auth.dto.GuestAuthResponseDTO;

@SpringBootTest
public class GuestServiceTest {

    @Autowired
    private GuestService guestService;

    @MockitoSpyBean
    private JwtService jwtService;

    @Test
    void continueAsGuest_successful() {
        GuestAuthResponseDTO response = guestService.continueAsGuest();

        assertTrue(response.username().matches("[A-Za-z]+[A-Za-z]+#\\d{3}"), "Guest name should match pattern 'AdjectiveNoun#Num'");

        verify(jwtService, times(1)).generateGuestToken(any(UserDetails.class)); 
    }

}
