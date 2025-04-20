package com.margot.word_map.controller.rest;

import com.margot.word_map.dto.request.ConfirmRequest;
import com.margot.word_map.dto.request.UserSignUpRequest;
import com.margot.word_map.dto.response.ConfirmResponse;
import com.margot.word_map.dto.response.TokenResponse;
import com.margot.word_map.exception.InvalidTokenException;
import com.margot.word_map.model.User;
import com.margot.word_map.service.auth.user.UserAuthService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/user")
public class UserAuthController {

    private final UserAuthService userAuthService;

    @Operation(
            summary = "Регистрация игрока",
            description = "Регистрация с указанием почты и юзернейма, отправка кода на почту",
            externalDocs = @ExternalDocumentation(
                    description = "документация в Confluence",
                    url = "Документация еще не прописана"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Успешная регистрация"),
                    @ApiResponse(responseCode = "400", description = "Введены невалидные данные", content = @Content)
            }
    )
    @PostMapping("/signup")
    public ConfirmResponse signUpUser(@RequestBody @Validated UserSignUpRequest request) {
        return userAuthService.signUpUser(request);
    }

    @Operation(
            summary = "Вход по почте для игроков",
            description = "Отправка кода подтверждения на введенную почту",
            externalDocs = @ExternalDocumentation(
                    description = "документация в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/" +
                            "152567831/GET+auth+login+email"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Успешный вход. Код успешно отправлен"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Нет доступа", content = @Content),
                    @ApiResponse(responseCode = "401",
                            description = "Почта введена в невалидном формате",
                            content = @Content
                    )
            }
    )
    @GetMapping("/login/{email}")
    public ConfirmResponse loginUser(@PathVariable @Email(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Invalid email format. Example: example@mail.com")
                                    @Parameter(description = "Почта пользователя", example = "mail123@gmail.com")
                                    @NotBlank String email) {
        return userAuthService.login(email);
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
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Указан неверный код", content = @Content)
            }
    )
    @PostMapping("/confirm")
    public TokenResponse verifyConfirmCode(
            @RequestBody @Validated ConfirmRequest confirmRequest
    ) {
        return userAuthService.verifyConfirmCodeAndGenerateTokens(confirmRequest.getEmail(), confirmRequest.getCode());
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
            throw new InvalidTokenException("refresh token is missing");
        }

        return userAuthService.refreshAccessToken(refreshToken);
    }

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
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
                    @ApiResponse(responseCode = "403", description = "Нет доступа")
            }
    )
    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal UserDetails userDetails) {
        userAuthService.logout(((User) userDetails).getId());
    }
}
