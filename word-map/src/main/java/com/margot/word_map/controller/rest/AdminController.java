package com.margot.word_map.controller.rest;

import com.margot.word_map.dto.AdminDto;
import com.margot.word_map.dto.AdminInfoDto;
import com.margot.word_map.dto.RuleDto;
import com.margot.word_map.dto.request.ChangeAdminAccessRequest;
import com.margot.word_map.dto.request.CreateAdminRequest;
import com.margot.word_map.dto.request.UpdateAdminRequest;
import com.margot.word_map.dto.response.GetAdminsResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "AdminsController",
        description = "Контроллер для получение прав и админов",
        externalDocs = @ExternalDocumentation(
                description = "Контроллер не прописан в доке, пока прост заглушечный по сути"
        )
)
@SecurityRequirement(name = "JWT")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final RuleService ruleService;

    private final AdminService adminService;

    @GetMapping("/rules")
    public List<RuleDto> getRules() {
        return ruleService.getRulesDto();
    }

    @GetMapping()
    public GetAdminsResponse getAdmins(@RequestParam(defaultValue = "0") @Min(0) Integer page,
                                       @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size) {
        return adminService.getAdmins(page, size);
    }

    @Operation(
            summary = "Создание админа/модератора",
            description = "запрос для создания админа/модератора",
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
    public void createAdmin(@RequestBody @Validated CreateAdminRequest request) {
        adminService.createAdmin(request);
    }

    @GetMapping("/{id}")
    public AdminDto getAdmin(@PathVariable Long id) {
        return adminService.getAdminDetailedInfoById(id);
    }

    @Operation(
            summary = "Метод редактирования пользователя административной панели",
            description = "1. возможность изменения роли (админ -> модератор и наоборот), " +
                    "для роли модератора указываются права," +
                    "2. возможность указания новых прав для модератора ",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/190742573/PUT+admin+id"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Данные обновлены успешно"),
                    @ApiResponse(responseCode = "401", description = "Токен доступа недействителен"),
                    @ApiResponse(responseCode = "401", description = "Несуществующее правило"),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден"),
            }
    )
    @PutMapping("/{id}")
    public void updateAdmin(@PathVariable Long id, @RequestBody @Valid UpdateAdminRequest request) {
        adminService.updateAdmin(id, request);
    }

    @GetMapping("/info")
    public AdminInfoDto getCurrentAdminInfo() {
        return adminService.getCurrentAdminInfo();
    }

    @PutMapping("/language/{id}")
    public void setLanguage(@PathVariable Long id) {
        adminService.updateCurrentAdminLanguage(id);
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
                    @ApiResponse(responseCode = "401", description = "Токен доступа недействителен"),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден"),
            }
    )
    @PutMapping("/access/{id}")
    public void changeAdminAccess(@PathVariable Long id, @RequestBody @Valid ChangeAdminAccessRequest request) {
        adminService.changeAccess(id, request);
    }
}
