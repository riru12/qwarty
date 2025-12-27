package com.qwarty.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.qwarty.auth.dto.LoginAuthRequestDTO;
import com.qwarty.auth.dto.SignupAuthRequestDTO;
import com.qwarty.auth.lov.UserStatus;
import com.qwarty.auth.model.RefreshToken;
import com.qwarty.auth.model.User;
import com.qwarty.auth.repository.RefreshTokenRepository;
import com.qwarty.auth.repository.UserRepository;
import com.qwarty.auth.util.CookieUtil;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

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

    @MockitoBean
    private CookieUtil cookieUtil;

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
    void login_shouldCallCookieUtilAndSaveRefreshToken() {
        String username = "user";
        String password = "pass";

        LoginAuthRequestDTO request = new LoginAuthRequestDTO(username, password);
        HttpServletResponse response = mock(HttpServletResponse.class);

        User user = User.builder()
                .username(username)
                .verified(true)
                .id(UUID.randomUUID())
                .build();
        when(userRepository.findByUsernameAndStatusNot(username, UserStatus.DELETED))
                .thenReturn(Optional.of(user));

        authService.login(request, response);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(cookieUtil).setAccessCookie(anyString(), any(Instant.class), eq(response));
        verify(cookieUtil).setRefreshCookie(anyString(), any(Instant.class), eq(response));
        verify(refreshTokenRepository).save(any(RefreshToken.class));
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
    void refresh_shouldRevokeOldTokenAndSetNewCookies() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).build();

        String oldRefreshToken = jwtService.generateRefreshToken(user);

        RefreshToken storedToken = RefreshToken.builder()
                .userId(userId)
                .tokenHash(hashTokenForTest(oldRefreshToken))
                .expiryDate(Instant.now().plusSeconds(3600))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByTokenHash(storedToken.getTokenHash())).thenReturn(Optional.of(storedToken));
        when(userRepository.findByIdAndStatusNot(userId, UserStatus.DELETED)).thenReturn(Optional.of(user));

        HttpServletResponse response = mock(HttpServletResponse.class);

        authService.refresh(oldRefreshToken, response);

        assertTrue(storedToken.isRevoked());
        verify(refreshTokenRepository).saveAll(anyList());
        verify(cookieUtil).setAccessCookie(anyString(), any(Instant.class), eq(response));
        verify(cookieUtil).setRefreshCookie(anyString(), any(Instant.class), eq(response));
    }

    @Test
    void refresh_missingToken_throwsAppException() {
        HttpServletResponse response = mock(HttpServletResponse.class);

        AppException exception = assertThrows(AppException.class, () -> authService.refresh(null, response));
        assertEquals(AppExceptionCode.REFRESH_TOKEN_MISSING, exception.getExceptionCode());

        exception = assertThrows(AppException.class, () -> authService.refresh("   ", response));
        assertEquals(AppExceptionCode.REFRESH_TOKEN_MISSING, exception.getExceptionCode());
    }

    @Test
    void refresh_invalidToken_throwsAppException() throws Exception {
        HttpServletResponse response = mock(HttpServletResponse.class);
        String fakeToken = "someInvalidToken";
        String hashedFakeToken = hashTokenForTest(fakeToken);

        when(refreshTokenRepository.findByTokenHash(hashedFakeToken)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> authService.refresh(fakeToken, response));
        assertEquals(AppExceptionCode.REFRESH_TOKEN_INVALID, exception.getExceptionCode());
    }

    @Test
    void refresh_expiredToken_throwsAppException() throws Exception {
        HttpServletResponse response = mock(HttpServletResponse.class);
        UUID userId = UUID.randomUUID();
        String oldToken = "expiredToken";
        String hashedToken = hashTokenForTest(oldToken);

        RefreshToken expiredToken = RefreshToken.builder()
                .userId(userId)
                .tokenHash(hashedToken)
                .expiryDate(Instant.now().minusSeconds(10))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByTokenHash(hashedToken)).thenReturn(Optional.of(expiredToken));

        AppException exception = assertThrows(AppException.class, () -> authService.refresh(oldToken, response));
        assertEquals(AppExceptionCode.REFRESH_TOKEN_EXPIRED, exception.getExceptionCode());
        verify(refreshTokenRepository).delete(expiredToken);
    }

    @Test
    void refresh_revokedToken_throwsAppException() throws Exception {
        HttpServletResponse response = mock(HttpServletResponse.class);
        UUID userId = UUID.randomUUID();
        String oldToken = "revokedToken";
        String hashedToken = hashTokenForTest(oldToken);

        RefreshToken revokedToken = RefreshToken.builder()
                .userId(userId)
                .tokenHash(hashedToken)
                .expiryDate(Instant.now().plusSeconds(1000))
                .revoked(true)
                .build();

        when(refreshTokenRepository.findByTokenHash(hashedToken)).thenReturn(Optional.of(revokedToken));

        AppException exception = assertThrows(AppException.class, () -> authService.refresh(oldToken, response));
        assertEquals(AppExceptionCode.REFRESH_TOKEN_REVOKED, exception.getExceptionCode());
    }

    @Test
    void logout_shouldClearCookieAndDeleteToken() throws Exception {
        UUID userId = UUID.randomUUID();
        String token = "refreshToken";
        String hashedToken = hashTokenForTest(token);

        RefreshToken storedToken = RefreshToken.builder()
                .userId(userId)
                .tokenHash(hashedToken)
                .expiryDate(Instant.now().plusSeconds(3600))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByTokenHash(hashedToken)).thenReturn(Optional.of(storedToken));
        HttpServletResponse response = mock(HttpServletResponse.class);

        authService.logout(token, response);

        verify(cookieUtil).clearRefreshCookie(response);
        verify(refreshTokenRepository).delete(storedToken);
    }
}
