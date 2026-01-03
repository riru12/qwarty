package com.qwarty.auth.controller;

import com.qwarty.auth.dto.LoginAuthRequestDTO;
import com.qwarty.auth.dto.SignupAuthRequestDTO;
import com.qwarty.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupAuthRequestDTO requestDto) {
        authService.signup(requestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginAuthRequestDTO requestDto, HttpServletResponse response) {
        authService.login(requestDto, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/session/refresh")
    public ResponseEntity<Void> refresh(
            @CookieValue(name = "refreshToken", required = true) String refreshToken, HttpServletResponse response) {
        authService.refresh(refreshToken, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/session/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = true) String refreshToken, HttpServletResponse response) {
        authService.logout(refreshToken, response);
        return ResponseEntity.ok().build();
    }
}
