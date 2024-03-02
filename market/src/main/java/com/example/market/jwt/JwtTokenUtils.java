package com.example.market.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.sql.Date;
import java.time.Instant;

@Slf4j
@Component
public class JwtTokenUtils {
    private final Key signingKey;
    private final JwtParser jwtParser;
    public JwtTokenUtils(
            @Value("${jwt.secret}")
            String jwtSecret
    ){
        log.info(jwtSecret);
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtParser = Jwts.parserBuilder().setSigningKey(this.signingKey).build();
    }

    public boolean validate(String token){
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.warn("invalid jwt");
        }
        return false;
    }

    public Claims parseClaims(String token){
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public String generateToken(UserDetails userDetails){
        Instant now = Instant.now();
        // jwt 무엇을 넣을지 Map 사용 가능
        Claims jwtClaims = Jwts.claims()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(24 * 60 * 60L)));

        return Jwts.builder()
                .setClaims(jwtClaims)
                .signWith(this.signingKey)
                .compact();
    }
}
