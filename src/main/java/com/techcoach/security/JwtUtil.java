package com.techcoach.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility component responsible for generating, decoding, validating, and extracting claims from JSON Web Tokens (JWT).
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    // Generates a signed JWT containing the user's email as the subject and an absolute expiration timestamp
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extracts the user email (subject) from a given JWT by passing a claims-resolution function
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extracts the expiration date from a given JWT
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Master extraction helper that parses token claims once and applies a functional resolver to retrieve specific data fields
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Decrypts and verifies the signature of a JWT using the application's secret key, returning all embedded claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Validates that the token's subject matches the user details and ensures the token has not expired
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Checks if the token's expiration date has passed relative to the current system time
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Decodes the Base64 encoded secret key into a cryptographic Key instance for HMAC-SHA algorithms
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}