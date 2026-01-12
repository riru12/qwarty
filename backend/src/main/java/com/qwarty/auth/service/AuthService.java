package com.qwarty.auth.service;

import com.qwarty.auth.dto.IdentityResponseDTO;
import com.qwarty.auth.dto.LoginRequestDTO;
import com.qwarty.auth.dto.SignupRequestDTO;
import com.qwarty.auth.lov.UserStatus;
import com.qwarty.auth.lov.UserType;
import com.qwarty.auth.model.User;
import com.qwarty.auth.repository.UserRepository;
import com.qwarty.auth.security.GuestAuthenticationToken;
import com.qwarty.exception.code.AppExceptionCode;
import com.qwarty.exception.code.FieldValidationExceptionCode;
import com.qwarty.exception.type.AppException;
import com.qwarty.exception.type.FieldValidationException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final int USER_SESSION_MAX_AGE = 7 * 24 * 60 * 60;
    private final int GUEST_SESSION_MAX_AGE = -1;
    private static final String[] ADJECTIVES = {"Speedy", "Sluggish", "Chill"};
    private static final String[] NOUNS = {"Turtle", "Rabbit", "Capybara"};

    /**
     * Registers a user after verifying that an existing account with the same username or email
     * doesn't exist
     */
    @Transactional
    public void signup(SignupRequestDTO requestDto) {
        validateSignup(requestDto);

        User user = User.builder()
                .username(requestDto.username())
                .email(requestDto.email())
                .passwordHash(passwordEncoder.encode(requestDto.password()))
                .build();

        userRepository.save(user);

        return;
    }

    /**
     * Logs a user in and returns a session cookie
     */
    public void login(LoginRequestDTO requestDto, HttpServletRequest request, HttpServletResponse response) {
        validateLogin(requestDto);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.username(), requestDto.password()));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);
        session.setAttribute("USERNAME", requestDto.username());
        session.setAttribute("USER_TYPE", UserType.USER);

        setSessionCookie(response, session.getId(), USER_SESSION_MAX_AGE);
    }

    /**
     * Logs a user out - invalidates a session if it exists from client request
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();

        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    /**
     * Guest session creation
     */
    public void guest(HttpServletRequest request, HttpServletResponse response) {
        String guestName = generateGuestName();

        GuestAuthenticationToken authentication = new GuestAuthenticationToken(guestName);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);
        session.setAttribute("USERNAME", guestName);
        session.setAttribute("USER_TYPE", UserType.GUEST);

        setSessionCookie(response, session.getId(), GUEST_SESSION_MAX_AGE);
    }

    private String generateGuestName() {
        String adjective = ADJECTIVES[new Random().nextInt(ADJECTIVES.length)];
        String noun = NOUNS[new Random().nextInt(NOUNS.length)];
        int number = new Random().nextInt(1000);
        return adjective + noun + "#" + String.format("%03d", number);
    }

    public IdentityResponseDTO me(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            UserType userType = (UserType) session.getAttribute("USER_TYPE");
            String username = (String) session.getAttribute("USERNAME");

            if (userType != null) {
                return new IdentityResponseDTO(username, userType);
            }
        }

        return new IdentityResponseDTO(null, UserType.ANON);
    }

    private void setSessionCookie(HttpServletResponse response, String sessionId, int maxAge) {
        Cookie sessionCookie = new Cookie("JSESSIONID", sessionId);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(true); // Use only over HTTPS
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(maxAge);
        response.addCookie(sessionCookie);
    }

    private void validateSignup(SignupRequestDTO requestDto) {
        List<FieldValidationExceptionCode> validationErrors = new ArrayList<>();
        if (userRepository.existsByUsernameAndStatusNot(requestDto.username(), UserStatus.DELETED)) {
            validationErrors.add(FieldValidationExceptionCode.USERNAME_ALREADY_REGISTERED);
        }
        if (userRepository.existsByEmailAndStatusNot(requestDto.email(), UserStatus.DELETED)) {
            validationErrors.add(FieldValidationExceptionCode.EMAIL_ALREADY_REGISTERED);
        }
        if (!validationErrors.isEmpty()) {
            throw new FieldValidationException(validationErrors);
        }
    }

    private void validateLogin(LoginRequestDTO requestDto) {
        User user = userRepository
                .findByUsernameAndStatusNot(requestDto.username(), UserStatus.DELETED)
                .orElseThrow(() -> new AppException(AppExceptionCode.USER_NOT_FOUND));

        if (!user.isVerified()) {
            throw new AppException(AppExceptionCode.USER_NOT_VERIFIED);
        }

        if (!passwordEncoder.matches(requestDto.password(), user.getPassword())) {
            throw new AppException(AppExceptionCode.INVALID_CREDENTIALS);
        }
    }
}
