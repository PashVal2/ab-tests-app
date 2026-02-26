package org.valdon.abtests.controller.auth;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.valdon.abtests.dto.auth.LoginRequest;
import org.valdon.abtests.dto.auth.LoginResponse;
import org.valdon.abtests.dto.user.UserRequest;
import org.valdon.abtests.dto.user.UserResponse;
import org.valdon.abtests.service.auth.AuthService;
import org.valdon.abtests.service.user.UserService;
import org.valdon.abtests.validation.OnCreate;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Validated(OnCreate.class) @RequestBody UserRequest user
    ) {
        userService.createUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) {
       return authService.login(loginRequest, response);
    }

}
