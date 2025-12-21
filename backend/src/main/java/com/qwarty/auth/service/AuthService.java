package com.qwarty.auth.service;

import com.qwarty.auth.dto.LoginAuthRequestDTO;
import com.qwarty.auth.dto.LoginAuthResponseDTO;
import com.qwarty.auth.dto.RefreshAuthRequestDTO;
import com.qwarty.auth.dto.RefreshAuthResponseDTO;
import com.qwarty.auth.dto.SignupAuthRequestDTO;
import com.qwarty.auth.model.RefreshToken;
import com.qwarty.auth.model.User;
import com.qwarty.auth.repository.RefreshTokenRepository;
import com.qwarty.auth.repository.UserRepository;
import com.qwarty.exception.CustomException;
import com.qwarty.exception.CustomExceptionCode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

    /**
     * Registers a user after verifying that an existing account with the same username or email
     * doesn't exist
     *
     * @param requestDto
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
     *
     * @param requestDto
     * @return JWT token in LoginAuthResponseDTO
     */
    @Transactional
    public LoginAuthResponseDTO login(LoginAuthRequestDTO requestDto) {
        User user = authenticate(requestDto);
        String accessJwt = jwtService.generateAccessToken(user);
        String refreshJwt = jwtService.generateRefreshToken(user);

        // save the refresh token granted to db
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(hashToken(refreshJwt))
                .expiryDate(jwtService.extractExpiration(refreshJwt).toInstant())
                .build();
        refreshTokenRepository.save(refreshToken);

        return new LoginAuthResponseDTO(accessJwt, refreshJwt);
    }

    @Transactional
    public RefreshAuthResponseDTO refresh(RefreshAuthRequestDTO requestDto) {
        String refreshToken = requestDto.refreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new CustomException(CustomExceptionCode.REFRESH_TOKEN_MISSING);
        }

        String hashedToken = hashToken(refreshToken); // hash the provided token from the client

        RefreshToken storedToken = refreshTokenRepository
                .findByTokenHash(hashedToken)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.REFRESH_TOKEN_INVALID));

        if (storedToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new CustomException(CustomExceptionCode.REFRESH_TOKEN_EXPIRED);
        }

        if (storedToken.isRevoked()) {
            throw new CustomException(CustomExceptionCode.REFRESH_TOKEN_REVOKED);
        }

        User user = userRepository
                .findById(storedToken.getUserId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.USER_NOT_FOUND));

        // client-provided refresh token is valid, generate new access and refresh tokens, and revoke old refresh token
        storedToken.setRevoked(true);

        String accessJwt = jwtService.generateAccessToken(user); // new access token
        String refreshJwt = jwtService.generateRefreshToken(user); // new refresh token
        RefreshToken newRefreshToken = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(hashToken(refreshJwt))
                .expiryDate(jwtService.extractExpiration(refreshJwt).toInstant())
                .build();

        refreshTokenRepository.saveAll(
                List.of(storedToken, newRefreshToken)); // update revoked old token, save new token

        return new RefreshAuthResponseDTO(accessJwt, refreshJwt);
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
     * @param token
     * @return hashed token
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
}
