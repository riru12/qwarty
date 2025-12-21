package com.qwarty.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/** JwtService - generates JWT, extracts claims from a JWT, checks JWT validity */
@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.access-expiration-time}")
    private long accessExpirationTime;

    @Value("${security.jwt.refresh-expiration-time}")
    private long refreshExpirationTime;

    private String ACCESS_TOKEN = "ACCESS";
    private String REFRESH_TOKEN = "REFRESH";

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", ACCESS_TOKEN);
        claims.put("guest", false);
        return buildToken(userDetails, claims, accessExpirationTime);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", REFRESH_TOKEN);
        claims.put("guest", false);
        return buildToken(userDetails, claims, refreshExpirationTime);
    }

    public String generateGuestToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", ACCESS_TOKEN);
        claims.put("guest", true);
        return buildToken(userDetails, claims, accessExpirationTime);
    }

    /**
     * Builds a JWT
     *
     * @param userDetails
     * @param claims
     * @param expiration
     * @return String JWT
     */
    private String buildToken(UserDetails userDetails, Map<String, Object> claims, long expiration) {
        return Jwts.builder()
                .claims()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .add(claims)
                .and()
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts username of user from token
     * @param token
     * @return String username
     */
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts expiration time in claims
     * @param token
     * @return Date expiration
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts type field in claims, returns String equivalent to either "ACCESS" or "REFRESH"
     * @param token
     * @return String type
     */
    public String extractType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    /**
     * From the entire payload, retrieve and return a specific claim attribute
     *
     * @param <T>
     * @param token
     * @param claimsResolver
     * @return <T> Generic type
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract the entire payload of the token
     *
     * @param token
     * @return Claims object
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Checks if a JWT is valid, also verifies that it's an access type.
     *
     * Used in JwtAuthFilter, to verify that requests from client use access token instead of the refresh token.
     *
     * @param token
     * @param userDetails
     * @return boolean
     */
    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractSubject(token);
            final String type = extractClaim(token, claims -> claims.get("type", String.class));
            return (ACCESS_TOKEN.equals(type) && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if a JWT is expired, If the token is malformed, it is caught with an exception and
     * returns true, to treat malformed tokens as expired by default
     *
     * @param token
     * @return boolean
     */
    private boolean isTokenExpired(String token) {
        try {
            return extractClaim(token, Claims::getExpiration).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Return SecretKey object decoded from `secretKey` variable
     *
     * @return SecretKey
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
