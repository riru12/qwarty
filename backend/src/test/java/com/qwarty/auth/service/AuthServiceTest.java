package com.qwarty.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.qwarty.auth.dto.LoginAuthRequestDTO;
import com.qwarty.auth.dto.LoginAuthResponseDTO;
import com.qwarty.auth.dto.SignupAuthRequestDTO;
import com.qwarty.auth.lov.UserStatus;
import com.qwarty.auth.model.RefreshToken;
import com.qwarty.auth.model.User;
import com.qwarty.auth.repository.RefreshTokenRepository;
import com.qwarty.auth.repository.UserRepository;
import com.qwarty.exception.code.AppExceptionCode;
import com.qwarty.exception.code.FieldValidationExceptionCode;
import com.qwarty.exception.type.AppException;
import com.qwarty.exception.type.FieldValidationException;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
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

    // Helper to compute the same hash the service uses
    private String hashTokenForTest(String token) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return Base64.getEncoder().encodeToString(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void signup_successfulRegistration() {
        String username = "newuser";
        String email = "new@example.com";
        String password = "password123";
        String passwordHash = "encodedPassword";

        SignupAuthRequestDTO request = new SignupAuthRequestDTO(username, email, password);

        when(userRepository.existsByUsernameAndStatusNot(username, UserStatus.DELETED))
                .thenReturn(false);
        when(userRepository.existsByEmailAndStatusNot(email, UserStatus.DELETED))
                .thenReturn(false);
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
    void signup_usernameAlreadyExists_throwsFieldValidationException() {
        String username = "existing";
        String email = "new@example.com";
        String password = "password123";

        SignupAuthRequestDTO request = new SignupAuthRequestDTO(username, email, password);

        when(userRepository.existsByUsernameAndStatusNot(username, UserStatus.DELETED))
                .thenReturn(true);

        FieldValidationException exception =
                assertThrows(FieldValidationException.class, () -> authService.signup(request));

        List<FieldValidationExceptionCode> fieldErrors = exception.getFieldErrors();
        assertEquals(1, fieldErrors.size());
        assertTrue(fieldErrors.contains(FieldValidationExceptionCode.USERNAME_ALREADY_REGISTERED));
    }

    @Test
    void signup_emailAlreadyExists_throwsFieldValidationException() {
        String username = "newuser";
        String email = "existing@example.com";
        String password = "password123";

        SignupAuthRequestDTO request = new SignupAuthRequestDTO(username, email, password);

        when(userRepository.existsByUsernameAndStatusNot(username, UserStatus.DELETED))
                .thenReturn(false);
        when(userRepository.existsByEmailAndStatusNot(email, UserStatus.DELETED))
                .thenReturn(true);

        FieldValidationException exception =
                assertThrows(FieldValidationException.class, () -> authService.signup(request));

        List<FieldValidationExceptionCode> fieldErrors = exception.getFieldErrors();
        assertEquals(1, fieldErrors.size());
        assertTrue(fieldErrors.contains(FieldValidationExceptionCode.EMAIL_ALREADY_REGISTERED));
    }

    @Test
    void login_successfulLogin_returnsJwt() {
        String username = "user";
        String password = "password123";

        LoginAuthRequestDTO request = new LoginAuthRequestDTO(username, password);
        HttpServletResponse response = mock(HttpServletResponse.class);

        User user = User.builder().username(username).verified(true).build();

        when(userRepository.findByUsernameAndStatusNot(username, UserStatus.DELETED))
                .thenReturn(Optional.of(user));

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        LoginAuthResponseDTO loginResponse = authService.login(request, response);

        assertEquals(username, loginResponse.username());

        verify(authenticationManager).authenticate(eq(new UsernamePasswordAuthenticationToken(username, password)));
    }

    @Test
    void login_userNotFound_throwsException() {
        String username = "unknown";
        String password = "password123";

        LoginAuthRequestDTO request = new LoginAuthRequestDTO(username, password);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(userRepository.findByUsernameAndStatusNot(username, UserStatus.DELETED))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> authService.login(request, response));
        assertEquals(AppExceptionCode.USER_NOT_FOUND, exception.getExceptionCode());
    }

    @Test
    void login_userNotVerified_throwsException() {
        String username = "unverified";
        String password = "password123";

        LoginAuthRequestDTO request = new LoginAuthRequestDTO(username, password);
        HttpServletResponse response = mock(HttpServletResponse.class);

        User user = User.builder().username(username).verified(false).build();

        when(userRepository.findByUsernameAndStatusNot(username, UserStatus.DELETED))
                .thenReturn(Optional.of(user));

        AppException exception = assertThrows(AppException.class, () -> authService.login(request, response));
        assertEquals(AppExceptionCode.USER_NOT_VERIFIED, exception.getExceptionCode());

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_invalidCredentials_authenticationFails_throwsException() {
        String username = "user";
        String password = "wrongpassword";

        LoginAuthRequestDTO request = new LoginAuthRequestDTO(username, password);
        HttpServletResponse response = mock(HttpServletResponse.class);

        User user = User.builder().username(username).verified(true).build();

        when(userRepository.findByUsernameAndStatusNot(username, UserStatus.DELETED))
                .thenReturn(Optional.of(user));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request, response));
    }

    @Test
    void refresh_successful_returnsNewAccessToken_andIssuesNewRefreshToken() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();

        String oldRefreshToken = jwtService.generateRefreshToken(user);
        String hashedOldToken = hashTokenForTest(oldRefreshToken);
        RefreshToken storedOldToken = RefreshToken.builder()
                .userId(userId)
                .tokenHash(hashedOldToken)
                .expiryDate(Instant.now().plusSeconds(7 * 24 * 3600))
                .revoked(false)
                .build();

        HttpServletResponse response = mock(HttpServletResponse.class);
        
        when(refreshTokenRepository.findByTokenHash(hashedOldToken))
                .thenReturn(Optional.of(storedOldToken));
        when(userRepository.findByIdAndStatusNot(userId, UserStatus.DELETED))
                .thenReturn(Optional.of(user));

        authService.refresh(oldRefreshToken, response);

        verify(refreshTokenRepository).saveAll(argThat(list -> {
            List<RefreshToken> tokens = (List<RefreshToken>) list;
            return tokens.size() == 2 &&
                tokens.stream().anyMatch(t -> ((RefreshToken) t).isRevoked()) &&
                tokens.stream().anyMatch(t -> !((RefreshToken) t).isRevoked());
        }));

        ArgumentCaptor<String> cookieCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), cookieCaptor.capture());
        
        String cookieHeader = cookieCaptor.getValue();
        assertTrue(cookieHeader.contains("refreshToken="));
        assertTrue(cookieHeader.contains("Path=/auth/refresh"));
        assertTrue(cookieHeader.contains("HttpOnly"));
        assertTrue(cookieHeader.contains("Max-Age="));
        assertFalse(cookieHeader.contains(oldRefreshToken));
    }

}
