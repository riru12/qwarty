package com.qwarty.auth.controller;

import com.qwarty.auth.dto.IdentityResponseDTO;
import com.qwarty.auth.service.IdentityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/me")
public class IdentityController {

    private final IdentityService identityService;

    @GetMapping
    public ResponseEntity<IdentityResponseDTO> me(
            @CookieValue(name = "accessToken", required = true) String accessToken) {

        IdentityResponseDTO identity = identityService.me(accessToken);
        return ResponseEntity.ok(identity);
    }
}
