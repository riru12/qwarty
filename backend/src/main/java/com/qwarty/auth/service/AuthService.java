package com.qwarty.auth.service;

import com.qwarty.auth.dto.LoginAuthRequestDTO;
import com.qwarty.auth.dto.LoginAuthResponseDTO;
import com.qwarty.auth.dto.SignupAuthRequestDTO;
import com.qwarty.auth.model.User;
import com.qwarty.auth.repository.UserRepository;
import com.qwarty.exception.CustomException;
import com.qwarty.exception.CustomExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a user after verifying that an existing account with the same username or email
     * doesn't exist
     *
     * @param requestDto
     */
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
    public LoginAuthResponseDTO login(LoginAuthRequestDTO requestDto) {
        User user = authenticate(requestDto);
        String jwt = jwtService.generateToken(user);
        return new LoginAuthResponseDTO(jwt);
    }

    public User authenticate(LoginAuthRequestDTO requestDto) {
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
}
