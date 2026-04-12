package org.valdon.abtests.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.valdon.abtests.dto.jwt.JwtUserRecord;
import org.valdon.abtests.ex.UnauthorizedException;
import org.valdon.abtests.config.props.JwtProperties;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;
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
        return createToken(
                user,
                "ACCESS",
                jwtProperties.getAccessTimeToLive(),
                claims -> claims.add("roles", user.roles())
        );
    }

    public String createRefreshToken(JwtUserRecord user) {
        return createToken(
                user,
                "REFRESH",
                jwtProperties.getRefreshTimeToLive(),
                null
        );
    }

    private String createToken(
            JwtUserRecord user,
            String type,
            Long ttl,
            Consumer<ClaimsBuilder> extraClaims
    ) {
        String iji = UUID.randomUUID().toString();

        ClaimsBuilder claimsBuilder = Jwts.claims()
                .id(iji)
                .issuedAt(Date.from(Instant.now()))
                .subject(user.id().toString())
                .add("type", type);
        if (extraClaims != null) {
            extraClaims.accept(claimsBuilder);
        }
        Instant expiredAt = Instant.now().plus(ttl, ChronoUnit.SECONDS);

        return Jwts.builder()
                .signWith(key)
                .claims(claimsBuilder.build())
                .expiration(Date.from(expiredAt))
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
            throw new UnauthorizedException("token has no roles");
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
