package com.qwarty.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

@Configuration
@RequiredArgsConstructor
public class SecurityFilterConfig {

    @Value("${allowed.origin}")
    private String allowedOrigin;

    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;
    private final AuthenticationProvider authenticationProvider;
    private final Logger logger = LoggerFactory.getLogger(SecurityFilterConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/**", "/i18n/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .authenticationProvider(authenticationProvider)
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(errorEntryPoint()).accessDeniedHandler(accessDeniedHandler()));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin(allowedOrigin);
        config.addAllowedHeader("Authorization");
        config.addAllowedHeader("Content-Type");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    /**
     * Creates an RFC 9457-compliant ProblemDetail to be returned by exception handlers and log the exception
     *
     * This handles exceptions separately from {@link #GlobalExceptionHandler} because Spring Security intercepts
     * requests before they reach the controller layer.
     */
    private void writeProblem(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpStatus status,
            String title,
            String detail,
            Exception exception)
            throws IOException {

        logger.warn("Exception occurred: {} - {}", exception.getClass().getSimpleName(), exception.getMessage());

        Locale locale = request.getLocale();
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle(messageSource.getMessage(title, null, locale));
        problemDetail.setDetail(messageSource.getMessage(detail, null, locale));

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), problemDetail);
    }

    /**
     * 401 – Authentication failure (unauthenticated)
     * Triggered when a request has missing, invalid, or expired credentials.
     */
    private AuthenticationEntryPoint errorEntryPoint() {
        return (request, response, authException) -> writeProblem(
                request,
                response,
                HttpStatus.UNAUTHORIZED,
                "auth.unauthorized.title",
                "auth.unauthorized.detail",
                authException);
    }

    /**
     * 403 – Authorization failure (forbidden)
     * Triggered when an authenticated user attempts to access a resource they don't have permission for.
     */
    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> writeProblem(
                request,
                response,
                HttpStatus.FORBIDDEN,
                "auth.forbidden.title",
                "auth.forbidden.detail",
                accessDeniedException);
    }
}
