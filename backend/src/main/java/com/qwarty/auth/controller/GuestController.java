package com.qwarty.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qwarty.auth.dto.GuestAuthResponseDTO;
import com.qwarty.auth.service.GuestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class GuestController {

    private final GuestService guestService;

    @GetMapping("/guest")
    public ResponseEntity<GuestAuthResponseDTO> guest() {
        GuestAuthResponseDTO responseDto = guestService.loginAsGuest();
        return ResponseEntity.ok(responseDto);
    }

}
