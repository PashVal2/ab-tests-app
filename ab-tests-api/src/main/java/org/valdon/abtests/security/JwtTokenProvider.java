package org.valdon.abtests.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.valdon.abtests.dto.jwt.JwtUserRecord;
import org.valdon.abtests.ex.JwtException;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtTokenService jwtTokenService;

    public Authentication getAuthentication(String token) {
        Claims claims = jwtTokenService.getClaims(token);
        if (!isAccessToken(claims)) {
            throw new JwtException("not valid token type");
        }
        Long id = jwtTokenService.getId(claims);
        Set<String> roles = jwtTokenService.getRoles(claims);
        Collection<GrantedAuthority> mappedRoles = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        UserPrincipal userDetail = UserPrincipal.builder()
                .id(id)
                .authorities(mappedRoles)
                .build();

        return new UsernamePasswordAuthenticationToken(
                userDetail,
                "",
                mappedRoles
        );
    }

    private boolean isAccessToken(Claims claims) {
        return jwtTokenService
                .getType(claims)
                .equalsIgnoreCase("ACCESS");
    }

    public String refreshToken(String refreshToken) {
        Claims claims = jwtTokenService.getClaims(refreshToken);
        Long id = jwtTokenService.getId(claims);
        return null;
    }

    public String createRefreshToken(JwtUserRecord user) {
        return jwtTokenService.createRefreshToken(user);
    }

    public String createAccessToken(JwtUserRecord user) {
        return jwtTokenService.createAccessToken(user);
    }

}
