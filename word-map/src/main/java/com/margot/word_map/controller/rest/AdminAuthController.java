package com.margot.word_map.controller.rest;

import com.margot.word_map.dto.request.AdminLoginRequest;
import com.margot.word_map.dto.request.ConfirmRequest;
import com.margot.word_map.dto.request.ConfirmResendRequest;
import com.margot.word_map.dto.request.RefreshTokenRequest;
import com.margot.word_map.dto.response.ConfirmResponse;
import com.margot.word_map.dto.response.TokenResponse;
import com.margot.word_map.service.admin.AdminService;
import com.margot.word_map.service.auth.AuthService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "AdminAuthController",
        description = "Контроллер для авторизации админов",
        externalDocs = @ExternalDocumentation(
                description = "Документация в Confluence"
        )
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/admin")
public class AdminAuthController {

    private final AuthService authService;

    private final AdminService adminService;

    @Operation(
            summary = "Вход по почте для админов",
            description = "Отправка кода подтверждения на введенную почту",
            externalDocs = @ExternalDocumentation(
                    description = "документация в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/" +
                            "152567831/POST+auth+admin+login"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Успешный вход. Код отправлен"),
                    @ApiResponse(responseCode = "400", description = "Некорректный формат почты", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован", content = @Content),
                    @ApiResponse(responseCode = "404",
                            description = "Аккаунт не найден",
                            content = @Content
                    )
            }
    )
    @PostMapping("/login")
    public ConfirmResponse loginAdmin(@Valid @RequestBody AdminLoginRequest request) {
        return authService.login(request.email());
    }

    @Operation(
            summary = "Метод для повторной генерации кода проверки",
            description = "Отправка кода подтверждения на введенную ранее почту",
            externalDocs = @ExternalDocumentation(
                    description = "документация в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/" +
                            "152567831/PUT+auth+admin+code+resend"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Успешный вход. Код отправлен"),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден", content = @Content),
                    @ApiResponse(responseCode = "404",
                            description = "Сессия подтверждения не найдена",
                            content = @Content
                    ),
                    @ApiResponse(responseCode = "429",
                            description = "Нельзя запросить новый код, пока не истек существующий",
                            content = @Content),
            }
    )
    @PutMapping("/code/resend")
    public ConfirmResponse resendCode(
            @Valid @RequestBody ConfirmResendRequest request) {
        return authService.resendConfirm(request.confirmID());
    }

    @Operation(
            summary = "Верификация кода подтверждения",
            description = "Верификация кода подтверждения, подтверждение входа",
            externalDocs = @ExternalDocumentation(
                    description = "документация в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/" +
                            "152338453/POST+auth+confirm"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Код подтвержден"),
                    @ApiResponse(responseCode = "410", description = "Истек срок жизни кода", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Сессия подтверждения не найдена",
                            content = @Content),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Неверный код подтверждения", content = @Content)
            }
    )
    @PostMapping("/confirm")
    public TokenResponse verifyConfirmCode(
            @Valid @RequestBody ConfirmRequest confirmRequest,
            @RequestHeader("User-Agent") String userAgent,
            HttpServletResponse response
    ) {
        return authService.verifyConfirmCodeAndGenerateTokens(confirmRequest, userAgent);
    }

    @Operation(
            summary = "Обновление access токена",
            description = "Обновление access токена с помощью refresh токена",
            externalDocs = @ExternalDocumentation(
                    description = "Метод не задокументирован в Confluence"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Access токен успешно обновлен"),
                    @ApiResponse(responseCode = "401", description = "Не указан refresh токен", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Невалидный refresh токен", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Устаревший refresh токен", content = @Content)
            }
    )
    @PostMapping("/refresh")
    public TokenResponse refreshAccessToken(
            @Valid @RequestBody(required = false) RefreshTokenRequest request,
            @RequestHeader(value = "User-Agent", defaultValue = "Unknown", required = false) String userAgent,
            HttpServletResponse response
    ) {
        return authService.refreshTokens(request.refreshToken(), userAgent);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Заявка на выход",
            description = "Удаление рефреш токена и выход",
            externalDocs = @ExternalDocumentation(
                    description = "документация в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/152535088/POST+auth+logout"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Успешный выход"),
                    @ApiResponse(responseCode = "401", description = "Сессия не найдена или истекла")
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logoutAdmin(
            @Valid @RequestBody(required = false) RefreshTokenRequest request) {
        authService.logout(request.refreshToken());

        return ResponseEntity.noContent().build();
    }
}
