package com.margot.word_map.controller.rest;

import com.margot.word_map.dto.request.AdminManagementRequest;
import com.margot.word_map.dto.request.ConfirmRequest;
import com.margot.word_map.dto.request.UserSignUpRequest;
import com.margot.word_map.dto.response.ConfirmResponse;
import com.margot.word_map.dto.response.TokenResponse;
import com.margot.word_map.exception.InvalidTokenException;
import com.margot.word_map.model.User;
import com.margot.word_map.service.auth.new_auth.admin.AdminAuthService;
import com.margot.word_map.service.auth.new_auth.admin.AdminService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/admin")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    private final AdminService adminService;

    @GetMapping("/login/{email}")
    public ConfirmResponse sendCode(@PathVariable @Email(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Invalid email format. Example: example@mail.com")
                                    @Parameter(description = "Почта пользователя", example = "mail123@gmail.com")
                                    @NotBlank String email) {
        return adminAuthService.login(email);
    }

    @PostMapping("/confirm")
    public TokenResponse verifyCode(
            @RequestBody @Validated ConfirmRequest confirmRequest
    ) {
        return adminAuthService.verifyConfirmCodeAndGenerateTokens(confirmRequest.getEmail(), confirmRequest.getCode());
    }

    @PostMapping("/refresh")
    public TokenResponse refreshToken(
            @Parameter(description = "кука со значением refresh токена")
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken == null) {
            throw new InvalidTokenException("refresh token is missing");
        }

        return adminAuthService.refreshAccessToken(refreshToken);
    }

    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal UserDetails userDetails) {
        adminAuthService.logout(((User) userDetails).getId());
    }

    @Operation(
            summary = "Управление админами",
            description = "Создать/обновить админа/модератора",
            externalDocs = @ExternalDocumentation(
                    description = "Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/190742573/POST+auth+admins"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Данные успешно обновлены"),
                    @ApiResponse(responseCode = "201", description = "Пользователь добавлен"),
                    @ApiResponse(responseCode = "400", description = "Невалидные данные"),
                    @ApiResponse(responseCode = "401", description = "Ошибка авторизации"),
                    @ApiResponse(responseCode = "403", description = "Нет доступа")
            }
    )
    @PostMapping("/admins")
    public ResponseEntity<Void> adminManagement(@RequestBody @Validated AdminManagementRequest request) {
        return new ResponseEntity<>(adminService.manageAdmin(request));
    }
}
