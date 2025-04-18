package com.margot.word_map.mapper;

import com.margot.word_map.dto.AdminDto;
import com.margot.word_map.model.Admin;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminMapper {

    private final RuleMapper ruleMapper;

    public AdminDto toDto(Admin admin) {
        return AdminDto.builder()
                .id(admin.getId())
                .email(admin.getEmail())
                .dateCreation(admin.getDateCreation())
                .dateActive(admin.getDateActive())
                .role(admin.getRole().name())
                .access(admin.getAccess())
                .adminRules(ruleMapper.toDto(admin.getRules()))
                .build();
    }

    public List<AdminDto> toDto(List<Admin> admins) {
        return admins.stream().map(this::toDto).toList();
    }

    public Page<AdminDto> toDto(Page<Admin> adminPage) {
        return adminPage.map(this::toDto);
    }
}
