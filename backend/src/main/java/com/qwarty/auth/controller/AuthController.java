package com.qwarty.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qwarty.auth.dto.SignupAuthRequestDTO;
import com.qwarty.auth.model.User;
import com.qwarty.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody SignupAuthRequestDTO requestDto) {
        User user = authService.signup(requestDto);
        return ResponseEntity.ok(user);
    }
}
