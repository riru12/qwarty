package com.qwarty.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qwarty.auth.dto.LoginAuthRequestDTO;
import com.qwarty.auth.dto.LoginAuthResponseDTO;
import com.qwarty.auth.dto.SignupAuthRequestDTO;
import com.qwarty.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupAuthRequestDTO requestDto) {
        authService.signup(requestDto);
        return ResponseEntity.ok()
            .build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginAuthResponseDTO> login(@RequestBody LoginAuthRequestDTO requestDto) {
        LoginAuthResponseDTO responseDto = authService.login(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
