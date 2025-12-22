package com.qwarty.auth.controller;

import com.qwarty.auth.dto.LoginAuthRequestDTO;
import com.qwarty.auth.dto.LoginAuthResponseDTO;
import com.qwarty.auth.dto.RefreshAuthRequestDTO;
import com.qwarty.auth.dto.RefreshAuthResponseDTO;
import com.qwarty.auth.dto.SignupAuthRequestDTO;
import com.qwarty.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<LoginAuthResponseDTO> login(@RequestBody LoginAuthRequestDTO requestDto) {
        LoginAuthResponseDTO responseDto = authService.login(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshAuthResponseDTO> refresh(@RequestBody RefreshAuthRequestDTO requestDto) {
        RefreshAuthResponseDTO responseDto = authService.refresh(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
