package com.margot.word_map.controller;

import com.margot.word_map.dto.AdminDto;
import com.margot.word_map.dto.RoleDto;
import com.margot.word_map.dto.response.GetAdminsResponse;
import com.margot.word_map.service.AdminService;
import com.margot.word_map.service.RoleService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/roles")
public class RoleModelController {

    private final RoleService roleService;

    private final AdminService adminService;

    @GetMapping
    public List<RoleDto> getRoles() {
        return roleService.getRoles();
    }

    @GetMapping("/admins")
    public GetAdminsResponse getAdmins(@RequestParam(defaultValue = "0") @Min(0) Integer page,
                                       @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size) {
        return adminService.getAdmins(page, size);
    }

    @GetMapping("/admins/{id}")
    public AdminDto getAdmin(@PathVariable Long id) {
        return adminService.getAdminById(id);
    }
}
