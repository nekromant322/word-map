package com.margot.word_map.service.auth.admin;

import com.margot.word_map.dto.AdminDto;
import com.margot.word_map.dto.RuleDto;
import com.margot.word_map.exception.AdminNotAccessException;
import com.margot.word_map.exception.BaseException;
import com.margot.word_map.model.Admin;
import com.margot.word_map.service.auth.generic_auth.AuthEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAuthEntityService implements AuthEntityService<AdminDto> {

    private final AdminService adminService;

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

    @Override
    public AdminDto getEntityById(Long id) {
        return adminService.getAdminInfoById(id);
    }

    @Override
    public BaseException createNoAccessException(String email) {
        return new AdminNotAccessException("admin " + email + " has no access");
    }

    @Override
    public String extractRole(AdminDto entity) {
        return entity.getRole();
    }

    @Override
    public List<String> extractRules(AdminDto entity) {
        return entity.getAdminRules().stream().map(RuleDto::getRule).toList();
    }
}
