package org.valdon.abtests.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.valdon.abtests.dto.auth.LoginRequest;
import org.valdon.abtests.dto.auth.TokenResponse;
import org.valdon.abtests.dto.auth.RegisterRequest;
import org.valdon.abtests.service.auth.JwtAuthService;
import org.valdon.abtests.service.auth.RegistrationService;
import org.valdon.abtests.service.user.UserService;
import org.valdon.abtests.validation.OnCreate;

import java.util.Map;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtAuthService jwtAuthService;
    private final RegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid @Validated(OnCreate.class) @RequestBody RegisterRequest user
    ) {
        registrationService.registerUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("/confirm-email")
    public ResponseEntity<Map<String, String>> confirmEmail(@RequestParam String token) {
        registrationService.verifyEmail(token);
        Map<String, String> response = Map.of("message", "E-mail успешно подтверждён");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public TokenResponse login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) {
       return jwtAuthService.login(loginRequest, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        jwtAuthService.logout(request, response);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(HttpServletRequest request) {
        return jwtAuthService.refresh(request);
    }

    @PostMapping("/resend-confirmation")
    public ResponseEntity<Void> resendConfirmEmail(
            @RequestParam String email
    ) {
        registrationService.resendConfirmation(email);
        return ResponseEntity.noContent().build();
    }

}
