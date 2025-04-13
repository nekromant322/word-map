package com.margot.word_map.controller.rest;

import com.margot.word_map.dto.AdminDto;
import com.margot.word_map.dto.RuleDto;
import com.margot.word_map.dto.response.GetAdminsResponse;
import com.margot.word_map.service.AdminService;
import com.margot.word_map.service.RuleService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return adminService.getAdminById(id);
    }
}
