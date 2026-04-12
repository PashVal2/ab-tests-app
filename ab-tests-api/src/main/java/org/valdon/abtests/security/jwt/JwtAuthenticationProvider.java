package org.valdon.abtests.security.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.valdon.abtests.ex.UnauthorizedException;
import org.valdon.abtests.security.UserPrincipal;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProvider {

    private final JwtTokenService jwtTokenService;

    public Authentication getAuthentication(String token) {
        Claims claims = jwtTokenService.getClaims(token);
        if (!"ACCESS".equals(jwtTokenService.getType(claims))) {
            throw new UnauthorizedException("not valid token type");
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

}
