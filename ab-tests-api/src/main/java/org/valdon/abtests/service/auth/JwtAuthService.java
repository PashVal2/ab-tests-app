package org.valdon.abtests.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.valdon.abtests.dto.auth.LoginRequest;
import org.valdon.abtests.dto.auth.TokenResponse;
import org.valdon.abtests.dto.auth.RegisterRequest;

public interface JwtAuthService {

    TokenResponse login(LoginRequest record, HttpServletResponse response);

    TokenResponse refresh(HttpServletRequest request);

    void logout(HttpServletRequest request, HttpServletResponse response);

}
