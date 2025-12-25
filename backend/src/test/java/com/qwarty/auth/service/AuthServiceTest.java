package com.qwarty.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.qwarty.auth.dto.LoginAuthRequestDTO;
import com.qwarty.auth.dto.LoginAuthResponseDTO;
import com.qwarty.auth.dto.SignupAuthRequestDTO;
import com.qwarty.auth.model.User;
import com.qwarty.auth.repository.RefreshTokenRepository;
import com.qwarty.auth.repository.UserRepository;
import com.qwarty.exception.CustomException;
import com.qwarty.exception.CustomExceptionCode;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @SuppressWarnings("unused")
    @Autowired // autowired to use injected values from application.properties
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Test
    void signup_successfulRegistration() {
        String username = "newuser";
        String email = "new@example.com";
        String password = "password123";
        String passwordHash = "encodedPassword";

        SignupAuthRequestDTO request = new SignupAuthRequestDTO(username, email, password);

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(passwordHash);

        User savedUser = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordHash)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        assertDoesNotThrow(() -> authService.signup(request));

        verify(userRepository)
                .save(argThat(user -> user.getUsername().equals(username)
                        && user.getEmail().equals(email)
                        && user.getPasswordHash().equals(passwordHash)));
    }

    @Test
    void signup_usernameAlreadyExists_throwsException() {
        String username = "existing";
        String email = "new@example.com";
        String password = "password123";

        SignupAuthRequestDTO request = new SignupAuthRequestDTO(username, email, password);

        when(userRepository.existsByUsername(username)).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () -> authService.signup(request));
        assertEquals(CustomExceptionCode.USERNAME_ALREADY_REGISTERED, exception.getExceptionCode());
    }

    @Test
    void signup_emailAlreadyExists_throwsException() {
        String username = "newuser";
        String email = "existing@example.com";
        String password = "password123";

        SignupAuthRequestDTO request = new SignupAuthRequestDTO(username, email, password);

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () -> authService.signup(request));
        assertEquals(CustomExceptionCode.EMAIL_ALREADY_REGISTERED, exception.getExceptionCode());
    }

    @Test
    void login_successfulLogin_returnsJwt() {
        String username = "user";
        String password = "password123";

        LoginAuthRequestDTO request = new LoginAuthRequestDTO(username, password);
        HttpServletResponse response = mock(HttpServletResponse.class);

        User user = User.builder().username(username).verified(true).build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        LoginAuthResponseDTO loginResponse = authService.login(request, response);

        assertNotNull(loginResponse);
        assertEquals(username, loginResponse.username());

        verify(authenticationManager).authenticate(eq(new UsernamePasswordAuthenticationToken(username, password)));
    }

    @Test
    void login_userNotFound_throwsException() {
        String username = "unknown";
        String password = "password123";

        LoginAuthRequestDTO request = new LoginAuthRequestDTO(username, password);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> authService.login(request, response));
        assertEquals(CustomExceptionCode.USER_NOT_FOUND, exception.getExceptionCode());
    }

    @Test
    void login_userNotVerified_throwsException() {
        String username = "unverified";
        String password = "password123";

        LoginAuthRequestDTO request = new LoginAuthRequestDTO(username, password);
        HttpServletResponse response = mock(HttpServletResponse.class);

        User user =
                User.builder().username(username).verified(false).build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        CustomException exception = assertThrows(CustomException.class, () -> authService.login(request, response));
        assertEquals(CustomExceptionCode.USER_NOT_VERIFIED, exception.getExceptionCode());

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_invalidCredentials_authenticationFails_throwsException() {
        String username = "user";
        String password = "wrongpassword";

        LoginAuthRequestDTO request = new LoginAuthRequestDTO(username, password);
        HttpServletResponse response = mock(HttpServletResponse.class);

        User user = User.builder().username(username).verified(true).build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request, response));
    }
}
