package com.qwarty.auth.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.qwarty.auth.dto.IdentityResponseDTO;
import com.qwarty.auth.service.IdentityService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class IdentityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IdentityService identityService;

    @Test
    void me_ShouldReturnOk_WhenAccessTokenCookieIsPresent() throws Exception {
        String accessToken = "valid-access-token";

        IdentityResponseDTO mockResponse = new IdentityResponseDTO("username", false);

        when(identityService.me(accessToken)).thenReturn(mockResponse);

        mockMvc.perform(get("/me").cookie(new Cookie("accessToken", accessToken)))
                .andExpect(status().isOk());

        verify(identityService, times(1)).me(accessToken);
    }
}
