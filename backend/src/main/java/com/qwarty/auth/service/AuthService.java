package com.qwarty.auth.service;

import com.qwarty.auth.dto.LoginAuthRequestDTO;
import com.qwarty.auth.dto.LoginAuthResponseDTO;
import com.qwarty.auth.dto.RefreshAuthResponseDTO;
import com.qwarty.auth.dto.SignupAuthRequestDTO;
import com.qwarty.auth.model.RefreshToken;
import com.qwarty.auth.model.User;
import com.qwarty.auth.repository.RefreshTokenRepository;
import com.qwarty.auth.repository.UserRepository;
import com.qwarty.exception.CustomException;
import com.qwarty.exception.CustomExceptionCode;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Value("${environment}")
    private String environment;

    /**
     * Registers a user after verifying that an existing account with the same username or email
     * doesn't exist
     */
    @Transactional
    public void signup(SignupAuthRequestDTO requestDto) {
        if (userRepository.existsByUsername(requestDto.username())) {
            throw new CustomException(CustomExceptionCode.USERNAME_ALREADY_REGISTERED);
        } else if (userRepository.existsByEmail(requestDto.email())) {
            throw new CustomException(CustomExceptionCode.EMAIL_ALREADY_REGISTERED);
        }

        User user = User.builder()
                .username(requestDto.username())
                .email(requestDto.email())
                .passwordHash(passwordEncoder.encode(requestDto.password()))
                .build();

        userRepository.save(user);

        return;
    }

    /**
     * Logs a user in and returns a JWT
     */
    @Transactional
    public LoginAuthResponseDTO login(LoginAuthRequestDTO requestDto, HttpServletResponse response) {
        User user = authenticate(requestDto);
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        Instant refreshExpiry =
                jwtService.extractExpiration(refreshToken).toInstant(); // extract expiry for db and cookie

        // save the refresh token granted to db
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(hashToken(refreshToken))
                .expiryDate(refreshExpiry)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        setRefreshCookie(refreshToken, refreshExpiry, response);

        return new LoginAuthResponseDTO(accessToken, user.getUsername());
    }

    @Transactional
    public RefreshAuthResponseDTO refresh(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new CustomException(CustomExceptionCode.REFRESH_TOKEN_MISSING);
        }

        String hashedToken = hashToken(refreshToken); // hash the provided token from the client

        RefreshToken storedRefreshTokenEntity = refreshTokenRepository
                .findByTokenHash(hashedToken)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.REFRESH_TOKEN_INVALID));

        if (storedRefreshTokenEntity.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(storedRefreshTokenEntity);
            throw new CustomException(CustomExceptionCode.REFRESH_TOKEN_EXPIRED);
        }

        if (storedRefreshTokenEntity.isRevoked()) {
            throw new CustomException(CustomExceptionCode.REFRESH_TOKEN_REVOKED);
        }

        User user = userRepository
                .findById(storedRefreshTokenEntity.getUserId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.USER_NOT_FOUND));

        // client-provided refresh token is valid, generate new access and refresh tokens, and revoke old refresh token
        storedRefreshTokenEntity.setRevoked(true);

        String newAccessToken = jwtService.generateAccessToken(user); // new access token
        String newRefreshToken = jwtService.generateRefreshToken(user); // new refresh token
        Instant newRefreshExpiry =
                jwtService.extractExpiration(newRefreshToken).toInstant(); // extract expiry for db and cookie

        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(hashToken(newRefreshToken))
                .expiryDate(newRefreshExpiry)
                .build();

        refreshTokenRepository.saveAll(
                List.of(storedRefreshTokenEntity, newRefreshTokenEntity)); // update revoked old token, save new token

        setRefreshCookie(newRefreshToken, newRefreshExpiry, response);

        return new RefreshAuthResponseDTO(newAccessToken);
    }

    private User authenticate(LoginAuthRequestDTO requestDto) {
        User user = userRepository
                .findByUsername(requestDto.username())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.USER_NOT_FOUND));

        if (!user.isVerified()) {
            throw new CustomException(CustomExceptionCode.USER_NOT_VERIFIED);
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.username(), requestDto.password()));

        return user;
    }

    /**
     * Primarily used for hashing a refresh token using SHA-256 before saving it to the DB
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Sets refreshToken cookie in the HTTP headers
     */
    private void setRefreshCookie(String refreshToken, Instant refreshTokenExpiry, HttpServletResponse response) {
        boolean isProd = "PROD".equals(environment);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(isProd ? true : false)
                .sameSite(isProd ? "Strict" : "Lax")
                .path("/auth/refresh")
                .maxAge(Duration.between(Instant.now(), refreshTokenExpiry))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}
