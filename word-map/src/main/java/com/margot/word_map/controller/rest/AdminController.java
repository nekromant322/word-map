package com.margot.word_map.controller.rest;

import com.margot.word_map.controller.rest.api.AdminApi;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController implements AdminApi {

    private final RuleService ruleService;

    private final AdminService adminService;

    @PostMapping("/list")
    @Override
    public PagedResponseDto<AdminListQueryDto> getAdmins(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestBody @Valid AdminSearchRequest request) {
        return adminService.getAdmins(PageRequest.of(page, size), request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public void createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        adminService.createAdmin(request);
    }

    @GetMapping("/{id}")
    @Override
    public AdminDto getAdmin(@PathVariable Long id) {
        return adminService.getAdminDetailedInfoById(id);
    }

    @PutMapping("/{id}")
    @Override
    public void updateAdmin(@PathVariable Long id, @RequestBody @Valid UpdateAdminRequest request) {
        adminService.updateAdmin(id, request);
    }

    @GetMapping("/info")
    @Override
    public AdminInfoDto getCurrentAdminInfo() {
        return adminService.getCurrentAdminInfo();
    }

    @PutMapping("/language/{id}")
    @Override
    public void setLanguage(@PathVariable Long id) {
        adminService.updateCurrentAdminLanguage(id);
    }

    @PutMapping("/access/{id}")
    @Override
    public void changeAdminAccess(@PathVariable Long id, @RequestBody @Valid ChangeAdminAccessRequest request) {
        adminService.changeAccess(id, request);
    }
}
