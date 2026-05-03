package com.erp.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TokenService {

    @Value("${jwt.secret:erp-secret-key-change-in-production-must-be-256-bits}")
    private String secret;

    @Value("${jwt.access-expiration:7200}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration:604800}")
    private long refreshExpiration;

    private SecretKey getKey() {
        byte[] keyBytes = new byte[32];
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(secretBytes, 0, keyBytes, 0, Math.min(secretBytes.length, 32));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Long userId, String username, List<String> permissions) {
        return Jwts.builder()
                .subject(userId.toString())
                .claims(Map.of("username", username, "permissions", permissions))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration * 1000))
                .signWith(getKey())
                .compact();
    }

    public String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration * 1000))
                .signWith(getKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
