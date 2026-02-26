package org.valdon.abtests.service.auth.Impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.valdon.abtests.dto.auth.LoginRequest;
import org.valdon.abtests.dto.auth.LoginResponse;
import org.valdon.abtests.dto.jwt.JwtUserRecord;
import org.valdon.abtests.security.JwtTokenProvider;
import org.valdon.abtests.security.UserPrincipal;
import org.valdon.abtests.service.auth.AuthService;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public LoginResponse login(LoginRequest loginData, HttpServletResponse response) {
        UserPrincipal userPrincipal = (UserPrincipal) authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginData.username(),
                        loginData.password()))
                .getPrincipal();

        Set<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        JwtUserRecord jwtUserRecord = new JwtUserRecord(userPrincipal.getId(), roles);
        String access = jwtTokenProvider.createAccessToken(jwtUserRecord);
        String refresh = jwtTokenProvider.createRefreshToken(jwtUserRecord);

        setTokenToCookie(refresh, response);
        return new LoginResponse(access);
    }

    private void setTokenToCookie(String refresh, HttpServletResponse response){
        Cookie cookie = new Cookie("REFRESH", refresh);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(24 * 60);
        cookie.setPath("/");

        response.addCookie(cookie);
    }

}
