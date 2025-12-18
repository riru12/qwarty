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

    @Value("${security.jwt.expiration-time}")
    private long expirationTime;

    /**
     * Generate a JWT with no additional custom claims
     *
     * @param userDetails
     * @return String JWT
     */
    public String generateToken(UserDetails userDetails) {
        return buildToken(userDetails, new HashMap<>(), expirationTime);
    }

    /**
     * Generate a JWT with custom claims
     *
     * @param userDetails
     * @param customClaims
     * @return String JWT
     */
    public String generateToken(UserDetails userDetails, Map<String, Object> customClaims) {
        return buildToken(userDetails, customClaims, expirationTime);
    }

    /**
     * Builds a JWT
     *
     * @param userDetails
     * @param customClaims
     * @param expiration
     * @return String JWT
     */
    private String buildToken(UserDetails userDetails, Map<String, Object> customClaims, long expiration) {
        return Jwts.builder()
                .claims()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .add(customClaims)
                .and()
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * From the entire payload, retrieve and return a specific claim attribute
     *
     * @param <T>
     * @param token
     * @param claimsResolver
     * @return <T> Generic type
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
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
     * Checks if a JWT is valid. If the token is malformed, it is caught with an exception and
     * returns false
     *
     * @param token
     * @param userDetails
     * @return boolean
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractClaim(token, Claims::getSubject);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
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
