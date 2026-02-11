package com.margot.word_map.controller.rest;

import com.margot.word_map.dto.AdminDto;
import com.margot.word_map.dto.AdminInfoDto;
import com.margot.word_map.dto.AdminListQueryDto;
import com.margot.word_map.dto.request.AdminSearchRequest;
import com.margot.word_map.dto.request.ChangeAdminAccessRequest;
import com.margot.word_map.dto.request.CreateAdminRequest;
import com.margot.word_map.dto.request.UpdateAdminRequest;
import com.margot.word_map.dto.response.PagedResponseDto;
import com.margot.word_map.service.admin.AdminService;
import com.margot.word_map.service.rule.RuleService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "AdminsController",
        description = "Контроллер для получения/редактирования информации о пользователях админ панели"
)
@SecurityRequirement(name = "JWT")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final RuleService ruleService;

    private final AdminService adminService;

    @Operation(
            summary = "Поиск админов/модераторов",
            description = "Метод получения списка пользователей административной панели по фильтрам.",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/385515841/POST+admin+list"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Данные возвращены успешно"),
                    @ApiResponse(responseCode = "400",
                            description = "Запрошенная страница превышает доступный диапазон"),
                    @ApiResponse(responseCode = "401", description = "Токен доступа недействителен"),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован"),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден"),
            }
    )
    @PostMapping("/list")
    public PagedResponseDto<AdminListQueryDto> getAdmins(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestBody @Valid AdminSearchRequest request) {
        return adminService.getAdmins(PageRequest.of(page, size), request);
    }

    @Operation(
            summary = "Создание админа/модератора",
            description = "Запрос для создания админа/модератора",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/204800006/POST+admin"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Админ/модератор создан"),
                    @ApiResponse(responseCode = "400", description = "Некорректный формат почты"),
                    @ApiResponse(responseCode = "401", description = "Токен доступа недействителен"),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден"),
                    @ApiResponse(responseCode = "404", description = "Несуществующее правило"),
                    @ApiResponse(responseCode = "409", description = "Электронная почта занята")
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        adminService.createAdmin(request);
    }

    @Operation(
            summary = "Детальная информация админа",
            description = "Метод получения детальной информации о пользователе административной панели",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/385417383/GET+admin+id"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Пользователь найден"),
                    @ApiResponse(responseCode = "401", description = "Токен доступа недействителен"),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован"),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден"),
                    @ApiResponse(responseCode = "404", description = "Админ не найден"),
            }
    )
    @GetMapping("/{id}")
    public AdminDto getAdmin(@PathVariable Long id) {
        return adminService.getAdminDetailedInfoById(id);
    }

    @Operation(
            summary = "Редактирование прав пользователя административной панели",
            description = "Метод редактирования пользователя административной панели, требуется роль admin",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/190742573/PUT+admin+id"
            ),
            security = @SecurityRequirement(name = "JWT", scopes = {"ROLE_ADMIN"})
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Данные обновлены успешно"),
                    @ApiResponse(responseCode = "401", description = "Токен доступа недействителен"),
                    @ApiResponse(responseCode = "401", description = "Несуществующее правило"),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден"),
                    @ApiResponse(responseCode = "404", description = "Админ не найден"),
            }
    )
    @PutMapping("/{id}")
    public void updateAdmin(@PathVariable Long id, @RequestBody @Valid UpdateAdminRequest request) {
        adminService.updateAdmin(id, request);
    }

    @Operation(
            summary = "Получение данных текущего пользователя",
            description = "Метод получения сквозных данных о пользователе административной панели",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/300810245/GET+admin+info"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "401", description = "Токен доступа недействителен"),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован"),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден"),
            }
    )
    @GetMapping("/info")
    public AdminInfoDto getCurrentAdminInfo() {
        return adminService.getCurrentAdminInfo();
    }

    @Operation(
            summary = "Метод изменения языка в хедере",
            description = "Метод изменения языка пользователем административной панели",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/152272959/PUT+admin+language+id"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "401", description = "Токен доступа недействителен"),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован"),
                    @ApiResponse(responseCode = "403", description = "Язык недоступен"),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден"),
            }
    )
    @PutMapping("/language/{id}")
    public void setLanguage(@PathVariable Long id) {
        adminService.updateCurrentAdminLanguage(id);
    }

    @SecurityRequirement(name = "JWT", scopes = {"ROLE_ADMIN"})
    @Operation(
            summary = "Изменение доступа админа/модератора",
            description = "Запрос позволяет пользователям с ролью admin запретить или разрешить доступ " +
                    "пользователям с ролью moderator",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/204603402/PUT+admin+access+id"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Доступ успешно изменен"),
                    @ApiResponse(responseCode = "401", description = "Токен доступа недействителен"),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден"),
                    @ApiResponse(responseCode = "404", description = "Админ не найден"),
            }
    )
    @PutMapping("/access/{id}")
    public void changeAdminAccess(@PathVariable Long id, @RequestBody @Valid ChangeAdminAccessRequest request) {
        adminService.changeAccess(id, request);
    }
}
