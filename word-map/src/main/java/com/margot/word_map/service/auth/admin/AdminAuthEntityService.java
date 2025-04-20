package com.margot.word_map.service.auth.admin;

import com.margot.word_map.dto.AdminDto;
import com.margot.word_map.exception.AdminNotAccessException;
import com.margot.word_map.model.Admin;
import com.margot.word_map.service.auth.generic_auth.AuthEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAuthEntityService implements AuthEntityService<AdminDto> {

    private final AdminService adminService;

    public AdminDto getAccessibleAdminDtoByEmail(String email) {
        AdminDto adminDto = adminService.getAdminInfoByEmail(email);

        if (!adminDto.getAccess()) {
            throw new AdminNotAccessException("admin has not access");
        }

        return adminDto;
    }

    public Admin getAccessibleAdminByEmail(String email) {
        Admin admin = adminService.getAdminByEmail(email);

        if (!admin.getAccess()) {
            throw new AdminNotAccessException("admin has not access");
        }

        return admin;
    }

    public List<String> getAdminRuleNames(Admin admin) {
        return admin.getRules().stream()
                .map(rule -> rule.getName().name())
                .toList();
    }

    public Admin getAdminById(Long id) {
        return adminService.getAdminById(id);
    }

    @Override
    public AdminDto getByEmail(String email) {
        return adminService.getAdminInfoByEmail(email);
    }

    @Override
    public Long getId(AdminDto entity) {
        return entity.getId();
    }

    @Override
    public boolean hasAccess(AdminDto entity) {
        return entity.getAccess();
    }

    @Override
    public String getEmail(AdminDto entity) {
        return entity.getEmail();
    }
}
