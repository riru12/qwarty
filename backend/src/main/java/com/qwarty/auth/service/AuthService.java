package com.qwarty.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.qwarty.auth.dto.SignupAuthRequestDTO;
import com.qwarty.auth.model.User;
import com.qwarty.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User signup(SignupAuthRequestDTO requestDto) {
        if (userRepository.existsByUsernameOrEmail(requestDto.getUsername(), requestDto.getEmail())) {
            throw new RuntimeException("Username or email already registered");
        }

        User user = User.builder()
            .username(requestDto.getUsername())
            .email(requestDto.getEmail())
            .passwordHash(passwordEncoder.encode(requestDto.getPassword()))
            .build();

        return userRepository.save(user);
    }

}
