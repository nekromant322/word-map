package com.margot.word_map.controller.rest;

import com.margot.word_map.dto.request.ChangeAdminAccessRequest;
import com.margot.word_map.dto.request.ConfirmRequest;
import com.margot.word_map.dto.request.CreateAdminRequest;
import com.margot.word_map.dto.request.UpdateAdminRequest;
import com.margot.word_map.dto.response.ConfirmResponse;
import com.margot.word_map.dto.response.TokenResponse;
import com.margot.word_map.exception.InvalidTokenException;
import com.margot.word_map.model.Admin;
import com.margot.word_map.service.admin.AdminService;
import com.margot.word_map.service.auth.AuthService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
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
                            "152567831/GET+auth+login+email"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Успешный вход. Код отправлен"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Нет доступа", content = @Content),
                    @ApiResponse(responseCode = "401",
                            description = "Почта введена в невалидном формате",
                            content = @Content
                    )
            }
    )
    @GetMapping("/login/{email}")
    public ConfirmResponse loginAdmin(@PathVariable @Email(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Invalid email format. Example: example@mail.com")
                                      @Parameter(description = "Почта пользователя", example = "mail123@gmail.com")
                                      @NotBlank String email) {
        return authService.login(email);
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
        return authService.verifyConfirmCodeAndGenerateTokens(confirmRequest.getEmail(), confirmRequest.getCode());
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
            @Parameter(description = "кука со значением refresh токена")
            @RequestHeader(value = "Refresh", required = true) String refreshToken
    ) {
        if (refreshToken == null) {
            throw new InvalidTokenException("refresh token is missing");
        }

        return authService.refreshAccessToken(refreshToken);
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
    public void logoutAdmin(@AuthenticationPrincipal UserDetails userDetails) {
        authService.logout(((Admin) userDetails).getId());
    }

    @Operation(
            summary = "Создание админа/модератора",
            description = "запрос для создания админа/модератора",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/204800006/POST+auth+admin"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Админ/модератор создан"),
                    @ApiResponse(responseCode = "400", description = "Невалидные данные"),
                    @ApiResponse(responseCode = "400", description = "Почта уже использована"),
                    @ApiResponse(responseCode = "401", description = "Ошибка авторизации"),
                    @ApiResponse(responseCode = "403", description = "Нет доступа")
            }
    )
    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public void createAdmin(@RequestBody @Validated CreateAdminRequest request) {
        adminService.createAdmin(request);
    }

    @Operation(
            summary = "Обновление роли/прав админа/модератора",
            description = "1. возможность изменения роли (админ -> модератор и наоборот), " +
                    "для роли модератора указываются права," +
                    "2. возможность указания новых прав для модератора ",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/190742573/PUT+auth+admin"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Данные обновлены успешно"),
                    @ApiResponse(responseCode = "404", description = "Админ с таким id не найден"),
                    @ApiResponse(responseCode = "400", description = "Невалидные данные"),
                    @ApiResponse(responseCode = "401", description = "Ошибка авторизации"),
                    @ApiResponse(responseCode = "403", description = "Нет доступа")
            }
    )
    @PutMapping("/admin")
    public void updateAdmin(@RequestBody @Validated UpdateAdminRequest request) {
        adminService.updateAdmin(request);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Изменение доступа админа/модератора",
            description = "Запрос позволяет поменять запретить или разрешить доступ админу/модератору",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/204603402/POST+auth+admin+access"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Доступ успешно изменен"),
                    @ApiResponse(responseCode = "404", description = "Админ с таким id не найден"),
                    @ApiResponse(responseCode = "400", description = "Невалидные данные"),
                    @ApiResponse(responseCode = "401", description = "Ошибка авторизации"),
                    @ApiResponse(responseCode = "403", description = "Нет доступа")
            }
    )
    @PostMapping("/admin/access")
    public void changeAdminAccess(@RequestBody @Validated ChangeAdminAccessRequest request) {
        adminService.changeAccess(request);
    }
}
