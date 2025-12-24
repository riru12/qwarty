package com.qwarty.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.qwarty.auth.dto.LoginAuthRequestDTO;
import com.qwarty.auth.dto.LoginAuthResponseDTO;
import com.qwarty.auth.dto.SignupAuthRequestDTO;
import com.qwarty.auth.lov.UserStatus;
import com.qwarty.auth.model.User;
import com.qwarty.auth.repository.RefreshTokenRepository;
import com.qwarty.auth.repository.UserRepository;
import com.qwarty.exception.CustomException;
import com.qwarty.exception.CustomExceptionCode;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
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
    private RefreshTokenRepository refreshTokenRepository;

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
        String email = "user@example.com";
        String password = "password123";
        String passwordHash = "encodedPassword";
        String accessToken = "access-token-123";
        String refreshToken = "refresh-token-123";
        Date refreshExpiration = new Date(System.currentTimeMillis() + 7200000);

        LoginAuthRequestDTO request = new LoginAuthRequestDTO(username, password);
        HttpServletResponse response = mock(HttpServletResponse.class);

        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordHash)
                .status(UserStatus.ACTIVE)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(user)).thenReturn((refreshToken));
        when(jwtService.extractExpiration(refreshToken)).thenReturn(refreshExpiration);

        // authenticationManager.authenticate should not throw
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        LoginAuthResponseDTO loginResponse = authService.login(request, response);

        assertNotNull(loginResponse);
        assertEquals(accessToken, loginResponse.accessToken());
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

        User user = User.builder().username(username).status(UserStatus.UNVERIFIED).build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        CustomException exception = assertThrows(CustomException.class, () -> authService.login(request, response));

        assertEquals(CustomExceptionCode.USER_NOT_VERIFIED, exception.getExceptionCode());

        // authenticationManager should not be called
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_invalidCredentials_authenticationFails_throwsException() {
        String username = "user";
        String password = "wrongpassword";

        LoginAuthRequestDTO request = new LoginAuthRequestDTO(username, password);
        HttpServletResponse response = mock(HttpServletResponse.class);

        User user = User.builder().username(username).status(UserStatus.ACTIVE).build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        doThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        // authenticate method will throw the authentication exception, which bubbles up
        assertThrows(AuthenticationException.class, () -> authService.login(request, response));
    }
}
