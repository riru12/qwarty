package com.qwarty.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.qwarty.auth.service.GuestService;
import jakarta.servlet.http.HttpServletResponse;
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
    void guest_ShouldReturnOkWithGuestCookie() throws Exception {
        String guestToken = "guest-token";

        // Mock the service to set the guest token cookie
        doAnswer(invocation -> {
                    HttpServletResponse response = invocation.getArgument(0);
                    response.addHeader("Set-Cookie", "accessToken=" + guestToken + "; HttpOnly; Path=/");
                    return null;
                })
                .when(guestService)
                .continueAsGuest(any(HttpServletResponse.class));

        mockMvc.perform(get("/auth/guest")).andExpect(status().isOk()).andExpect(cookie().exists("accessToken"));

        verify(guestService, times(1)).continueAsGuest(any(HttpServletResponse.class));
    }
}
