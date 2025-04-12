package com.margot.word_map.controller.rest;

import com.margot.word_map.dto.request.AdminManagementRequest;
import com.margot.word_map.dto.request.ConfirmRequest;
import com.margot.word_map.dto.response.ConfirmResponse;
import com.margot.word_map.dto.response.TokenResponse;
import com.margot.word_map.model.Admin;
import com.margot.word_map.service.AdminService;
import com.margot.word_map.service.auth.AuthService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Tag(
        name = "AuthController",
        description = "Контроллер для авторизации админов",
        externalDocs = @ExternalDocumentation(
                description = "Документация в Confluence",
                url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/152535064/MS+-+auth"
        )
)
@RestController
@RequestMapping("/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final AdminService adminService;

    @Operation(
            summary = "Заявка на вход по почте",
            description = "Отправка кода подтверждения на введенную почту",
            externalDocs = @ExternalDocumentation(
                    description = "Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/" +
                            "152567831/GET+auth+login+email"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Код успешно отправлен"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Нет доступа", content = @Content)
            }
    )
    @GetMapping("/login/{email}")
    public ConfirmResponse sendCode(@PathVariable @Email(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Invalid email format. Example: example@mail.com")
                                    @Parameter(description = "Почта пользователя", example = "mail123@gmail.com")
                                    @NotBlank String email) {
        return authService.sendVerificationCode(email);
    }

    @Operation(
            summary = "Верификация кода подтверждения",
            description = "Верификация кода подтверждения, подтверждение входа",
            externalDocs = @ExternalDocumentation(
                    description = "Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/" +
                            "152338453/POST+auth+confirm"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Код подтвержден"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Код не найден", content = @Content)
            }
    )
    @PostMapping("/confirm")
    public TokenResponse verifyCode(
            @RequestBody @Validated ConfirmRequest confirmRequest
    ) {
        return authService.verifyCodeAndGenerateToken(confirmRequest.getEmail(), confirmRequest.getCode());
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
    public TokenResponse refreshToken(
            @Parameter(description = "кука со значением refresh токена")
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Refresh token is missing");
        }

        return authService.refreshAccessToken(refreshToken);
    }

    @Operation(
            summary = "Заявка на выход",
            description = "Удаление рефреш токена и выход",
            externalDocs = @ExternalDocumentation(
                    description = "Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/152535088/POST+auth+logout"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Успешный выход"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
                    @ApiResponse(responseCode = "403", description = "Нет доступа")
            }
    )
    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails instanceof Admin admin) {
            authService.logout(admin.getId());
        } else {
            // toDo раскомментить когда будет юзер
//            User user = (User) userDetails;
//            authService.deleteRefreshTokenByUserId(user.getId());
        }
    }

    @PostMapping("/admins")
    public void adminManagement(@RequestBody @Validated AdminManagementRequest request) {
        adminService.manageAdmin(request);
    }
}

