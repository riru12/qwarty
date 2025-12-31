package com.qwarty.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.qwarty.auth.dto.LoginAuthRequestDTO;
import com.qwarty.auth.dto.LoginAuthResponseDTO;
import com.qwarty.auth.dto.RefreshAuthResponseDTO;
import com.qwarty.auth.dto.SignupAuthRequestDTO;
import com.qwarty.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private final String username = "testuser";
    private final String email = "test@example.com";
    private final String password = "password123";
    private final String accessToken = "access-token";
    private final String refreshToken = "refresh-token";
    private final String newAccessToken = "new-access-token";
    private final String newRefreshToken = "new-refresh-token";
    private final String refreshCookieName = "refreshToken";
    private final String refreshCookiePath = "/auth/refresh";

    @Test
    void signup_ShouldReturnOk_WhenValidRequest() throws Exception {
        SignupAuthRequestDTO requestDto = new SignupAuthRequestDTO(username, email, password);
        doNothing().when(authService).signup(any(SignupAuthRequestDTO.class));

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(authService, times(1)).signup(any(SignupAuthRequestDTO.class));
    }

    @Test
    void login_ShouldReturnOkWithAccessTokenAndCookie_WhenValidRequest() throws Exception {
        LoginAuthRequestDTO requestDto = new LoginAuthRequestDTO(username, password);
        LoginAuthResponseDTO responseDto = new LoginAuthResponseDTO(accessToken, username);

        when(authService.login(any(LoginAuthRequestDTO.class), any(HttpServletResponse.class)))
                .thenAnswer(invocation -> {
                    HttpServletResponse response = invocation.getArgument(1);
                    response.addHeader(
                            "Set-Cookie",
                            refreshCookieName + "=" + refreshToken + "; HttpOnly; Path=" + refreshCookiePath);
                    return responseDto;
                });

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(accessToken))
                .andExpect(cookie().exists(refreshCookieName));

        verify(authService, times(1)).login(any(LoginAuthRequestDTO.class), any(HttpServletResponse.class));
    }

    @Test
    void refresh_ShouldReturnOkWithNewAccessTokenAndCookie_WhenValidToken() throws Exception {
        RefreshAuthResponseDTO responseDto = new RefreshAuthResponseDTO(newAccessToken);

        when(authService.refresh(eq(refreshToken), any(HttpServletResponse.class)))
                .thenAnswer(invocation -> {
                    HttpServletResponse response = invocation.getArgument(1);
                    response.addHeader(
                            "Set-Cookie",
                            refreshCookieName + "=" + newRefreshToken + "; HttpOnly; Path=" + refreshCookiePath);
                    return responseDto;
                });

        mockMvc.perform(get("/auth/refresh").cookie(new Cookie(refreshCookieName, refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(newAccessToken))
                .andExpect(cookie().exists(refreshCookieName));

        verify(authService, times(1)).refresh(eq(refreshToken), any(HttpServletResponse.class));
    }

    @Test
    void signup_ShouldReturnBadRequest_WhenFieldIsMissing() throws Exception {
        SignupAuthRequestDTO[] testCases = {
            new SignupAuthRequestDTO(null, email, password),
            new SignupAuthRequestDTO(username, null, password),
            new SignupAuthRequestDTO(username, email, null)
        };

        for (SignupAuthRequestDTO requestDto : testCases) {
            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());

            verify(authService, times(0)).signup(any());
        }
    }

    @Test
    void login_ShouldReturnBadRequest_WhenFieldIsMissing() throws Exception {
        LoginAuthRequestDTO[] testCases = {
            new LoginAuthRequestDTO(null, password), new LoginAuthRequestDTO(username, null)
        };

        for (LoginAuthRequestDTO requestDto : testCases) {
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());

            verify(authService, times(0)).login(any(LoginAuthRequestDTO.class), any(HttpServletResponse.class));
        }
    }

    @Test
    void refresh_ShouldReturnBadRequest_WhenCookieMissing() throws Exception {
        mockMvc.perform(get("/auth/refresh")) // no cookie
                .andExpect(status().isBadRequest());

        verify(authService, times(0)).refresh(any(), any(HttpServletResponse.class));
    }

    @Test
    void logout_ShouldReturnOk_WhenValidToken() throws Exception {
        doNothing().when(authService).logout(eq(refreshToken), any(HttpServletResponse.class));

        mockMvc.perform(post("/auth/session/logout").cookie(new Cookie(refreshCookieName, refreshToken)))
                .andExpect(status().isOk());

        verify(authService, times(1)).logout(eq(refreshToken), any(HttpServletResponse.class));
    }

    @Test
    void logout_ShouldReturnBadRequest_WhenCookieMissing() throws Exception {
        mockMvc.perform(post("/auth/session/logout")).andExpect(status().isBadRequest());

        verify(authService, times(0)).logout(any(), any(HttpServletResponse.class));
    }
}
