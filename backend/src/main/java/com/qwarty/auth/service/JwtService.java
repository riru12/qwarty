package com.qwarty.auth.service;

import com.qwarty.auth.lov.JwtTokenType;
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

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.access-expiration-time}")
    private long accessExpirationTime;

    @Value("${security.jwt.refresh-expiration-time}")
    private long refreshExpirationTime;

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", JwtTokenType.ACCESS.name());
        claims.put("guest", false);
        return buildToken(userDetails, claims, accessExpirationTime);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", JwtTokenType.REFRESH.name());
        claims.put("guest", false);
        return buildToken(userDetails, claims, refreshExpirationTime);
    }

    public String generateGuestToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", JwtTokenType.ACCESS.name());
        claims.put("guest", true);
        return buildToken(userDetails, claims, accessExpirationTime);
    }

    /**
     * Builds a JWT
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
     */
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts expiration time in claims
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts type field in claims, returns String equivalent to either "ACCESS" or "REFRESH"
     */
    public JwtTokenType extractType(String token) {
        String typeString = extractClaim(token, claims -> claims.get("type", String.class));
        return JwtTokenType.valueOf(typeString);
    }

    /**
     * From the entire payload, retrieve and return a specific claim attribute
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract the entire payload of the token
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
     */
    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractSubject(token);
            final JwtTokenType type =
                    JwtTokenType.valueOf(extractClaim(token, claims -> claims.get("type", String.class)));
            return (JwtTokenType.ACCESS.equals(type)
                    && username.equals(userDetails.getUsername())
                    && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if a JWT is expired, If the token is malformed, it is caught with an exception and
     * returns true, to treat malformed tokens as expired by default
     */
    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Return SecretKey object decoded from `secretKey` variable
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
