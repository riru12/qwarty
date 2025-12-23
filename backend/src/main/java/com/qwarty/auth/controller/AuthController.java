package com.qwarty.auth.controller;

import com.qwarty.auth.dto.LoginAuthRequestDTO;
import com.qwarty.auth.dto.LoginAuthResponseDTO;
import com.qwarty.auth.dto.RefreshAuthResponseDTO;
import com.qwarty.auth.dto.SignupAuthRequestDTO;
import com.qwarty.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<Void> signup(@RequestBody SignupAuthRequestDTO requestDto) {
        authService.signup(requestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginAuthResponseDTO> login(
            @RequestBody LoginAuthRequestDTO requestDto, HttpServletResponse response) {
        LoginAuthResponseDTO responseDto = authService.login(requestDto, response);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/refresh")
    public ResponseEntity<RefreshAuthResponseDTO> refresh(
            @CookieValue(name = "refreshToken", required = true) String refreshToken, HttpServletResponse response) {
        RefreshAuthResponseDTO responseDto = authService.refresh(refreshToken, response);
        return ResponseEntity.ok(responseDto);
    }
}
