package com.qwarty.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.qwarty.auth.dto.IdentityResponseDTO;
import com.qwarty.auth.dto.LoginRequestDTO;
import com.qwarty.auth.dto.SignupRequestDTO;
import com.qwarty.auth.lov.UserType;
import com.qwarty.auth.service.AuthService;
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

    @Test
    void signup_ShouldReturnOk_WhenValidRequest() throws Exception {
        SignupRequestDTO requestDto = new SignupRequestDTO(username, email, password);
        doNothing().when(authService).signup(any(SignupRequestDTO.class));

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(authService, times(1)).signup(any(SignupRequestDTO.class));
    }

    @Test
    void login_ShouldReturnOk_WhenValidRequest() throws Exception {
        LoginRequestDTO requestDto = new LoginRequestDTO(username, password);
        doNothing().when(authService).login(any(LoginRequestDTO.class), any());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(authService, times(1)).login(any(LoginRequestDTO.class), any());
    }

    @Test
    void guest_ShouldReturnOk() throws Exception {
        doNothing().when(authService).guest(any());

        mockMvc.perform(post("/auth/guest")).andExpect(status().isOk());

        verify(authService, times(1)).guest(any());
    }

    @Test
    void me_ShouldReturnIdentityResponseDto() throws Exception {
        IdentityResponseDTO dummyResponse = new IdentityResponseDTO("username", UserType.USER);

        when(authService.me(any())).thenReturn(dummyResponse);

        mockMvc.perform(get("/auth/me")).andExpect(status().isOk());

        verify(authService, times(1)).me(any());
    }

    @Test
    void signup_ShouldReturnBadRequest_WhenFieldIsMissing() throws Exception {
        SignupRequestDTO[] testCases = {
            new SignupRequestDTO(null, email, password),
            new SignupRequestDTO(username, null, password),
            new SignupRequestDTO(username, email, null)
        };

        for (SignupRequestDTO requestDto : testCases) {
            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());

            verify(authService, times(0)).signup(any());
        }
    }

    @Test
    void login_ShouldReturnBadRequest_WhenFieldIsMissing() throws Exception {
        LoginRequestDTO[] testCases = {new LoginRequestDTO(null, password), new LoginRequestDTO(username, null)};

        for (LoginRequestDTO requestDto : testCases) {
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());

            verify(authService, times(0)).login(any(), any());
        }
    }
}
