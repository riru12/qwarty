package com.qwarty.auth.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.qwarty.auth.dto.GuestAuthResponseDTO;
import com.qwarty.auth.service.GuestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class GuestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GuestService guestService;

    @Test
    void guest_ShouldReturnOkAndGuestDto() throws Exception {
        String guestToken = "guest-token";
        String username = "RandomUsername#100";

        GuestAuthResponseDTO responseDto = new GuestAuthResponseDTO(guestToken, username);

        when(guestService.continueAsGuest()).thenReturn(responseDto);

        mockMvc.perform(get("/auth/guest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(guestToken));

        verify(guestService, times(1)).continueAsGuest();
    }
}
