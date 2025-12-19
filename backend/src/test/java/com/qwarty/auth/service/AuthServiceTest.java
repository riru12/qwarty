package com.qwarty.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.qwarty.auth.dto.LoginAuthRequestDTO;
import com.qwarty.auth.dto.LoginAuthResponseDTO;
import com.qwarty.auth.dto.SignupAuthRequestDTO;
import com.qwarty.auth.model.User;
import com.qwarty.auth.repository.UserRepository;
import com.qwarty.exception.CustomException;
import com.qwarty.exception.CustomExceptionCode;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void signup_successfulRegistration() {
        SignupAuthRequestDTO request = new SignupAuthRequestDTO("newuser", "new@example.com", "password123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        User savedUser = User.builder()
                .username("newuser")
                .email("new@example.com")
                .passwordHash("encodedPassword")
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // should not throw
        assertDoesNotThrow(() -> authService.signup(request));

        verify(userRepository)
                .save(argThat(user -> user.getUsername().equals("newuser")
                        && user.getEmail().equals("new@example.com")
                        && user.getPasswordHash().equals("encodedPassword")));
    }

    @Test
    void signup_usernameAlreadyExists_throwsException() {
        SignupAuthRequestDTO request = new SignupAuthRequestDTO("existing", "new@example.com", "password");

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () -> authService.signup(request));

        assertEquals(CustomExceptionCode.USERNAME_ALREADY_REGISTERED, exception.getExceptionCode());
    }

    @Test
    void signup_emailAlreadyExists_throwsException() {
        SignupAuthRequestDTO request = new SignupAuthRequestDTO("newuser", "existing@example.com", "password");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () -> authService.signup(request));

        assertEquals(CustomExceptionCode.EMAIL_ALREADY_REGISTERED, exception.getExceptionCode());
    }

    @Test
    void login_successfulLogin_returnsJwt() {
        LoginAuthRequestDTO request = new LoginAuthRequestDTO("user", "password");

        User user = User.builder()
                .username("user")
                .email("user@example.com")
                .passwordHash("encoded")
                .verified(true)
                .build();

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token-123");

        // authenticationManager.authenticate should not throw
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        LoginAuthResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token-123", response.token());

        verify(authenticationManager).authenticate(eq(new UsernamePasswordAuthenticationToken("user", "password")));
    }

    @Test
    void login_userNotFound_throwsException() {
        LoginAuthRequestDTO request = new LoginAuthRequestDTO("unknown", "password");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> authService.login(request));

        assertEquals(CustomExceptionCode.USER_NOT_FOUND, exception.getExceptionCode());
    }

    @Test
    void login_userNotVerified_throwsException() {
        LoginAuthRequestDTO request = new LoginAuthRequestDTO("unverified", "password");

        User user = User.builder().username("unverified").verified(false).build();

        when(userRepository.findByUsername("unverified")).thenReturn(Optional.of(user));

        CustomException exception = assertThrows(CustomException.class, () -> authService.login(request));

        assertEquals(CustomExceptionCode.USER_NOT_VERIFIED, exception.getExceptionCode());

        // authenticationManager should not be called
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_invalidCredentials_authenticationFails_throwsException() {
        LoginAuthRequestDTO request = new LoginAuthRequestDTO("user", "wrongpassword");

        User user = User.builder().username("user").verified(true).build();

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        doThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        // authenticate method will throw the authentication exception, which bubbles up
        assertThrows(AuthenticationException.class, () -> authService.login(request));
    }
}
