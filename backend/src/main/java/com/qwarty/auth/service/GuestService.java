package com.qwarty.auth.service;

import com.qwarty.auth.model.User;
import com.qwarty.auth.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final JwtService jwtService;
    private final CookieUtil cookieUtil;
    private static final String[] ADJECTIVES = {"Speedy", "Sluggish", "Chill"};
    private static final String[] NOUNS = {"Turtle", "Rabbit", "Capybara"};

    public void continueAsGuest(HttpServletResponse response) {
        String guestName = generateGuestName();

        UserDetails guestDetails = User.builder().username(guestName).build();
        String guestToken = jwtService.generateGuestToken(guestDetails);
        Instant guestExpiry = jwtService.extractExpiration(guestToken).toInstant();

        cookieUtil.setAccessCookie(guestToken, guestExpiry, response);
    }

    private String generateGuestName() {
        String adjective = ADJECTIVES[new Random().nextInt(ADJECTIVES.length)];
        String noun = NOUNS[new Random().nextInt(NOUNS.length)];
        int number = new Random().nextInt(1000);
        return adjective + noun + "#" + String.format("%03d", number);
    }
}
