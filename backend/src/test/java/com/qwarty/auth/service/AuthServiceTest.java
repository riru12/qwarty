package com.qwarty.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.qwarty.auth.dto.IdentityResponseDTO;
import com.qwarty.auth.dto.LoginRequestDTO;
import com.qwarty.auth.dto.SignupRequestDTO;
import com.qwarty.auth.model.User;
import com.qwarty.auth.repository.UserRepository;
import com.qwarty.exception.code.AppExceptionCode;
import com.qwarty.exception.type.AppException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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
        HttpSession session = mock(HttpSession.class);

        LoginRequestDTO dto = new LoginRequestDTO(username, password);
        User user = User.builder().username(username).verified(true).build();

        when(request.getSession(true)).thenReturn(session);
        when(userRepository.findByUsernameAndStatusNot(any(), any())).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        assertDoesNotThrow(() -> authService.login(dto, request));
        verify(session).setAttribute(eq("SPRING_SECURITY_CONTEXT"), any());
    }

    @Test
    void guest_shouldSetGuestSession() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession(true)).thenReturn(session);

        authService.guest(request);

        verify(session).setAttribute(eq("USERNAME"), any());
        verify(session).setAttribute("IS_GUEST", true);
    }

    @Test
    void me_shouldReturnGuestIdentity() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("IS_GUEST")).thenReturn(true);
        when(session.getAttribute("USERNAME")).thenReturn(guestUsername);

        IdentityResponseDTO dto = authService.me(request);
        assertEquals(guestUsername, dto.username());
        assertTrue(dto.isGuest());
    }

    @Test
    void me_shouldReturnUserIdentity() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        Authentication auth = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);

        SecurityContextHolder.setContext(context);

        User user = User.builder().username(username).build();

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("IS_GUEST")).thenReturn(null);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(user);
        when(context.getAuthentication()).thenReturn(auth);

        IdentityResponseDTO dto = authService.me(request);
        assertEquals(username, dto.username());
        assertFalse(dto.isGuest());
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

        AppException ex = assertThrows(AppException.class, () -> authService.me(request));
        assertEquals(AppExceptionCode.NO_SESSION, ex.getExceptionCode());
    }
}
