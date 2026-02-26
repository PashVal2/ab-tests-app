package org.valdon.abtests.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.valdon.abtests.dto.jwt.JwtUserRecord;
import org.valdon.abtests.ex.JwtException;
import org.valdon.abtests.props.JwtProperties;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtProperties jwtProperties;
    private SecretKey key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public String createAccessToken(JwtUserRecord user) {
        String iji = UUID.randomUUID().toString();

        Claims claims = Jwts.claims()
                .id(iji)
                .subject(String.valueOf(user.id()))
                .issuedAt(Date.from(Instant.now()))
                .add("roles", user.roles())
                .add("type", "ACCESS")
                .build();

        Instant validTime = Instant.now().plus(
                jwtProperties.getAccessTimeToLive(),
                ChronoUnit.MINUTES);

        return Jwts.builder()
                .claims(claims)
                .expiration(Date.from(validTime))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(JwtUserRecord user) {
        String iji = UUID.randomUUID().toString();

        Claims claims = Jwts.claims()
                .id(iji)
                .subject(String.valueOf(user.id()))
                .issuedAt(Date.from(Instant.now()))
                .add("type", "REFRESH")
                .build();

        Instant validTime = Instant.now().plus(
                jwtProperties.getRefreshTimeToLive(),
                ChronoUnit.MINUTES);

        return Jwts.builder()
                .claims(claims)
                .expiration(Date.from(validTime))
                .signWith(key)
                .compact();
    }

    public String getType(Claims claims) {
        return claims.get("type", String.class);
    }

    public Long getId(Claims claims) {
        return Long.valueOf(claims.getSubject());
    }

    public String getIji(Claims claims) {
        return claims.getId();
    }

    public Date getExpiration(Claims claims) {
        return claims.getExpiration();
    }

    public Set<String> getRoles(Claims claims) {
        Object rolesObject = claims.get("roles");

        if (!(rolesObject instanceof Collection<?> collection)) {
            throw new JwtException("token has no roles");
        }

        return collection.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
