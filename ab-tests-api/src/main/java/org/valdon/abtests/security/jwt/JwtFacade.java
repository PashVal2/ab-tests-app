package org.valdon.abtests.security.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.valdon.abtests.dto.jwt.JwtTokens;
import org.valdon.abtests.dto.jwt.JwtUserRecord;
import org.valdon.abtests.ex.UnauthorizedException;
import org.valdon.abtests.security.UserPrincipal;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtFacade {

    private final JwtTokenService jwtTokenService;
    private final JwtTokenRedisService jwtRedisService;

    public JwtTokens generateTokens(UserPrincipal userPrincipal) {

        Set<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        JwtUserRecord record = new JwtUserRecord(userPrincipal.getId(), roles);

        String access = jwtTokenService.createAccessToken(record);
        String refresh = jwtTokenService.createRefreshToken(record);

        return new JwtTokens(access, refresh);
    }

    public void validateRefreshAndGetUserId(String refreshToken) {
        Claims claims = jwtTokenService.getClaims(refreshToken);

        if (!"REFRESH".equals(jwtTokenService.getType(claims))) {
            throw new UnauthorizedException("Wrong token type");
        }
        if (jwtRedisService.isBlackListed(jwtTokenService.getIji(claims))) {
            throw new UnauthorizedException("Refresh token in black list");
        }
    }

    public Long getUserId(String refreshToken) {
        Claims claims = jwtTokenService.getClaims(refreshToken);
        return jwtTokenService.getId(claims);
    }

    public void blacklistRefreshToken(String refreshToken) {

        Claims claims = jwtTokenService.getClaims(refreshToken);

        Instant expiration = jwtTokenService.getExpiration(claims).toInstant();
        Instant now = Instant.now();

        jwtRedisService.invalidateToken(
                jwtTokenService.getIji(claims),
                Duration.between(now, expiration)
        );
    }

    public String createAccessToken(Long userId, Set<String> roles) {
        return jwtTokenService.createAccessToken(new JwtUserRecord(userId, roles));
    }

}
