package com.payflow.payflow.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility class responsible for generating and validating JWT tokens.
 * Acts as the core engine for authentication — creates tokens on login,
 * and verifies them on every subsequent protected request.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Converts the plain-text secret from application.properties into a
     * SecretKey object usable by the signing algorithm.
     */

    private SecretKey getSigningKey()
    {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generates a new JWT token for a given user email.
     *
     * @param email the user's email — becomes the token's subject
     * @return a signed JWT token as a String
     */
    public String generateToken(String email)
    {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime()+expiration);
        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    /**
     * Extracts the email (subject) stored inside a valid token.
     *
     * @param token the JWT token
     * @return the email embedded in the token
     */
    public String extractEmail(String token)
    {
        Claims claim = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claim.getSubject();
    }

    /**
     * Checks whether a token is valid — signature is correct and it hasn't expired.
     *
     * @param token the JWT token
     * @return true if valid, false otherwise
     */
    public boolean isTokenValid(String token)
    {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch (Exception e)
        {
            return false;
        }
    }
}
