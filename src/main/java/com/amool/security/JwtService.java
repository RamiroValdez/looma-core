package com.amool.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final long EXP_SECONDS = 3600;

    @Value("${JWT_SECRET:}")
    private String secretBase64;

    private Key key() {
        if (secretBase64 == null || secretBase64.isBlank()) {
            throw new IllegalStateException("La propiedad `JWT_SECRET` no está definida");
        }
        byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(EXP_SECONDS)))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
