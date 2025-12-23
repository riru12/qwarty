package com.qwarty.auth.service;

import com.qwarty.auth.dto.GuestAuthResponseDTO;
import com.qwarty.auth.model.User;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final JwtService jwtService;
    private static final String[] ADJECTIVES = {"Speedy", "Sluggish", "Chill"};
    private static final String[] NOUNS = {"Turtle", "Rabbit", "Capybara"};

    public GuestAuthResponseDTO loginAsGuest() {
        String guestName = generateGuestName();

        UserDetails guestDetails = User.builder().username(guestName).build();
        String guestToken = jwtService.generateGuestToken(guestDetails);
        return new GuestAuthResponseDTO(guestToken, guestName);
    }

    private String generateGuestName() {
        String adjective = ADJECTIVES[new Random().nextInt(ADJECTIVES.length)];
        String noun = NOUNS[new Random().nextInt(NOUNS.length)];
        int number = new Random().nextInt(1000);
        return adjective + noun + "#" + String.format("%03d", number);
    }
}
