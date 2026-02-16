package com.margot.word_map.controller.rest;

import com.margot.word_map.controller.rest.api.AdminAuthApi;
import com.margot.word_map.dto.request.AdminLoginRequest;
import com.margot.word_map.dto.request.ConfirmRequest;
import com.margot.word_map.dto.request.ConfirmResendRequest;
import com.margot.word_map.dto.request.RefreshTokenRequest;
import com.margot.word_map.dto.response.ConfirmResponse;
import com.margot.word_map.dto.response.TokenResponse;
import com.margot.word_map.service.admin.AdminService;
import com.margot.word_map.service.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/admin")
public class AdminAuthController implements AdminAuthApi {

    private final AuthService authService;

    private final AdminService adminService;

    @PostMapping("/login")
    @Override
    public ConfirmResponse loginAdmin(@Valid @RequestBody AdminLoginRequest request) {
        return authService.login(request.email());
    }

    @PutMapping("/code/resend")
    @Override
    public ConfirmResponse resendCode(
            @Valid @RequestBody ConfirmResendRequest request) {
        return authService.resendConfirm(request.confirmID());
    }

    @PostMapping("/confirm")
    @Override
    public TokenResponse verifyConfirmCode(
            @Valid @RequestBody ConfirmRequest confirmRequest,
            @RequestHeader("User-Agent") String userAgent,
            HttpServletResponse response
    ) {
        return authService.verifyConfirmCodeAndGenerateTokens(confirmRequest, userAgent);
    }

    @PostMapping("/refresh")
    @Override
    public TokenResponse refreshAccessToken(
            @Valid @RequestBody(required = false) RefreshTokenRequest request,
            @RequestHeader(value = "User-Agent", defaultValue = "Unknown", required = false) String userAgent,
            HttpServletResponse response
    ) {
        return authService.refreshTokens(request.refreshToken(), userAgent);
    }

    @PostMapping("/logout")
    @Override
    public ResponseEntity<Void> logoutAdmin(
            @Valid @RequestBody(required = false) RefreshTokenRequest request) {
        authService.logout(request.refreshToken());

        return ResponseEntity.noContent().build();
    }
}
