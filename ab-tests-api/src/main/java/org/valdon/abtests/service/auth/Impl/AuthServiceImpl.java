package org.valdon.abtests.service.auth.Impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.valdon.abtests.dto.auth.LoginRequest;
import org.valdon.abtests.dto.auth.TokenResponse;
import org.valdon.abtests.dto.jwt.JwtTokens;
import org.valdon.abtests.ex.UnauthorizedException;
import org.valdon.abtests.config.props.JwtProperties;
import org.valdon.abtests.security.jwt.JwtFacade;
import org.valdon.abtests.security.UserPrincipal;
import org.valdon.abtests.service.auth.JwtAuthService;
import org.valdon.abtests.service.cookie.CookieService;
import org.valdon.abtests.service.user.UserService;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements JwtAuthService {

    private final JwtFacade jwtFacade;
    private final UserService userService;
    private final CookieService cookieService;
    private final JwtProperties jwtProperties;
    private final AuthenticationManager authenticationManager;

    private static final String REFRESH_TOKEN = "REFRESH";

    @Override
    public TokenResponse login(LoginRequest request, HttpServletResponse response) {
        UserPrincipal userPrincipal = (UserPrincipal) authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()))
                .getPrincipal();

        JwtTokens tokens = jwtFacade.generateTokens(userPrincipal);
        cookieService.setValueToCookie(REFRESH_TOKEN, tokens.refresh(), response, (int) jwtProperties.getRefreshTimeToLive());
        return new TokenResponse(tokens.access());
    }

    @Override
    public TokenResponse refresh(HttpServletRequest request) {
        String refresh = cookieService.getValueFromCookie(REFRESH_TOKEN, request)
                .orElseThrow(() -> new UnauthorizedException("Refresh token not found"));
        jwtFacade.validateRefreshAndGetUserId(refresh);

        Long userId = jwtFacade.getUserId(refresh);
        Set<String> roles = userService.getRolesByUserId(userId);
        String access = jwtFacade.createAccessToken(userId, roles);
        return new TokenResponse(access);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        String refresh = cookieService.getValueFromCookie(REFRESH_TOKEN, request)
                .orElseThrow(() -> new UnauthorizedException("Refresh token not found"));

        jwtFacade.blacklistRefreshToken(refresh);

        cookieService.deleteCookie(REFRESH_TOKEN, response);
    }

}