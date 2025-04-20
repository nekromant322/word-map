package com.margot.word_map.controller.rest;

import com.margot.word_map.dto.AdminDto;
import com.margot.word_map.dto.RuleDto;
import com.margot.word_map.dto.response.GetAdminsResponse;
import com.margot.word_map.service.auth.admin.AdminService;
import com.margot.word_map.service.RuleService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "AdminsController",
        description = "Контроллер для получение прав и админов",
        externalDocs = @ExternalDocumentation(
                description = "Контроллер не прописан в доке, пока прост заглушечный по сути"
        )
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/admins")
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

    @GetMapping("/{id}")
    public AdminDto getAdmin(@PathVariable Long id) {
        return adminService.getAdminInfoById(id);
    }
}
