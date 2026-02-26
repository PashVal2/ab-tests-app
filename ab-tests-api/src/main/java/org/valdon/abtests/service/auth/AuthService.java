package org.valdon.abtests.service.auth;

import jakarta.servlet.http.HttpServletResponse;
import org.valdon.abtests.dto.auth.LoginRequest;
import org.valdon.abtests.dto.auth.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest record, HttpServletResponse response);

}
