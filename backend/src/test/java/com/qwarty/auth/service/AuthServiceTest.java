package com.qwarty.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.qwarty.auth.dto.IdentityResponseDTO;
import com.qwarty.auth.dto.LoginRequestDTO;
import com.qwarty.auth.dto.SignupRequestDTO;
import com.qwarty.auth.lov.UserType;
import com.qwarty.auth.model.User;
import com.qwarty.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    String username = "username";
    String guestUsername = "guest";
    String email = "sample@email.com";
    String password = "password";
    String encodedPassword = "encodedPassword";

    @Test
    void signup_shouldSaveUser() {
        SignupRequestDTO dto = new SignupRequestDTO(username, email, password);

        when(userRepository.existsByUsernameAndStatusNot(any(), any())).thenReturn(false);
        when(userRepository.existsByEmailAndStatusNot(any(), any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn(encodedPassword);

        assertDoesNotThrow(() -> authService.signup(dto));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void login_shouldSetSession() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        LoginRequestDTO dto = new LoginRequestDTO(username, password);
        User user = User.builder().username(username).verified(true).build();

        when(request.getSession(true)).thenReturn(session);
        when(userRepository.findByUsernameAndStatusNot(any(), any())).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        assertDoesNotThrow(() -> authService.login(dto, request, response));
        verify(session).setAttribute(eq("USERNAME"), any());
        verify(session).setAttribute("USER_TYPE", UserType.USER);
        verify(session).setAttribute(eq("SPRING_SECURITY_CONTEXT"), any());
    }

    @Test
    void guest_shouldSetGuestSession() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession(true)).thenReturn(session);

        authService.guest(request, response);

        verify(session).setAttribute(eq("USERNAME"), any());
        verify(session).setAttribute("USER_TYPE", UserType.GUEST);
        verify(session).setAttribute(eq("SPRING_SECURITY_CONTEXT"), any());
    }

    @Test
    void me_shouldReturnGuestIdentity() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER_TYPE")).thenReturn(UserType.GUEST); // updated
        when(session.getAttribute("USERNAME")).thenReturn(guestUsername);

        IdentityResponseDTO dto = authService.me(request);
        assertEquals(guestUsername, dto.username());
        assertEquals(UserType.GUEST, dto.userType());
    }

    @Test
    void me_shouldReturnUserIdentity() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER_TYPE")).thenReturn(UserType.USER); // updated
        when(session.getAttribute("USERNAME")).thenReturn(username);

        IdentityResponseDTO dto = authService.me(request);
        assertEquals(username, dto.username());
        assertEquals(UserType.USER, dto.userType());
    }

    @Test
    void logout_shouldInvalidateSessionAndClearCookie() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession(false)).thenReturn(session);

        authService.logout(request, response);

        verify(session).invalidate();
        verify(response).addCookie(any());
    }

    @Test
    void me_noSession_shouldThrow() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        SecurityContextHolder.clearContext();

        when(request.getSession(false)).thenReturn(null);

        IdentityResponseDTO dto = authService.me(request);

        assertNull(dto.username());
        assertEquals(UserType.ANON, dto.userType());
    }
}
