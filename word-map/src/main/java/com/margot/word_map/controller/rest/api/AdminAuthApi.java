package com.margot.word_map.controller.rest.api;

import com.margot.word_map.dto.request.AdminLoginRequest;
import com.margot.word_map.dto.request.ConfirmRequest;
import com.margot.word_map.dto.request.ConfirmResendRequest;
import com.margot.word_map.dto.request.RefreshTokenRequest;
import com.margot.word_map.dto.response.ConfirmResponse;
import com.margot.word_map.dto.response.TokenResponse;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(
        name = "AdminAuthController",
        description = "Контроллер для авторизации админов",
        externalDocs = @ExternalDocumentation(
                description = "Документация в Confluence"
        )
)
public interface AdminAuthApi {
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
    ConfirmResponse loginAdmin(@Valid @RequestBody AdminLoginRequest request);

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
    ConfirmResponse resendCode(@Valid @RequestBody ConfirmResendRequest request);

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
    TokenResponse verifyConfirmCode(
            @Valid @RequestBody ConfirmRequest confirmRequest,
            @RequestHeader("User-Agent") String userAgent,
            HttpServletResponse response);

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
    TokenResponse refreshAccessToken(
            @Valid @RequestBody(required = false) RefreshTokenRequest request,
            @RequestHeader(value = "User-Agent", defaultValue = "Unknown", required = false) String userAgent,
            HttpServletResponse response);

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
    ResponseEntity<Void> logoutAdmin(@Valid @RequestBody(required = false) RefreshTokenRequest request);
}
