package com.margot.word_map.service.jwt;

import com.margot.word_map.model.Admin;
import com.margot.word_map.model.Rule;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration.access-token-expiration}")
    private Duration accessTokenExpiration;
    @Value("${jwt.expiration.refresh-token-expiration}")
    private Duration refreshTokenExpiration;

    public String generateToken(String email, Admin.ROLE role, List<Rule.RULE> rules, Duration expiration) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .claim("rules", rules)
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(expiration)))
                .signWith(getKey())
                .compact();
    }

    public String generateAccessToken(String email, Admin.ROLE role, List<Rule.RULE> rules) {
        return generateToken(email, role, rules, accessTokenExpiration);
    }

    public String generateRefreshToken(String email) {
        return generateToken(email, null, null, refreshTokenExpiration);
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
}

