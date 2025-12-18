package com.qwarty.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.qwarty.auth.dto.LoginAuthRequestDTO;
import com.qwarty.auth.dto.LoginAuthResponseDTO;
import com.qwarty.auth.dto.SignupAuthRequestDTO;
import com.qwarty.auth.model.User;
import com.qwarty.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a user after verifying that an existing account with the same
     * username or email doesn't exist
     * 
     * @param requestDto
     */
    public void signup(SignupAuthRequestDTO requestDto) {
        if (userRepository.existsByUsernameOrEmail(requestDto.username(), requestDto.email())) {
            throw new RuntimeException("Username or email already registered");
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
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isVerified()) {
            throw new RuntimeException("Account not verified. Please verify your account.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.username(),
                        requestDto.password()));

        return user;
    }

}
