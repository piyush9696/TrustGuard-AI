package com.piyush.trustguard.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService
{
    private final SecretKey secretKey;
    private final long expiration;
    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration
    )
    {
        this.secretKey= Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration=expiration;
    }
    public String generateToken(String email)
    {
        Date now=new Date();
        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(new Date(now.getTime()+expiration))
                .signWith(secretKey)
                .compact();
    }
    public String extractEmail(String token)
    {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    private boolean isTokenExpired(String token) {

        Date expiration = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();

        return expiration.before(new Date());
    }
    public boolean isTokenValid(String token,String email)
    {
        String extractedEmail=extractEmail(token);
        return extractedEmail.equals(email)&&!isTokenExpired(token);
    }
}
