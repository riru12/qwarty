package com.qwarty.auth.service;

import com.qwarty.auth.dto.IdentityResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdentityService {

    private final JwtService jwtService;

    public IdentityResponseDTO me(String accessToken) {
        String username = jwtService.extractSubject(accessToken);
        boolean isGuest = jwtService.isGuestToken(accessToken);

        return new IdentityResponseDTO(username, isGuest);
    }
}
