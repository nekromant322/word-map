package com.margot.word_map.controller;

import com.margot.word_map.dto.ConfirmRequest;
import com.margot.word_map.dto.ConfirmResponse;
import com.margot.word_map.dto.TokenResponse;
import com.margot.word_map.service.auth_service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login/{email}")
    public ResponseEntity<ConfirmResponse> sendCode(@PathVariable @Email(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Invalid email format. Example: example@mail.com")
                                                    @NotBlank String email) {
        ConfirmResponse response = authService.sendVerificationCode(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<TokenResponse> verifyCode(
            @RequestBody ConfirmRequest confirmRequest) {
        TokenResponse tokenResponse =
                authService.verifyCodeAndGenerateToken(confirmRequest.getEmail(), confirmRequest.getCode());

        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Refresh token is missing");
        }

        TokenResponse tokenResponse = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/google-login")
    public void googleLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @GetMapping("/yandex-login")
    public void yandexLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/yandex");
    }
}

