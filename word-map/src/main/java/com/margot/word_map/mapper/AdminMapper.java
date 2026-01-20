package com.margot.word_map.mapper;

import com.margot.word_map.dto.AdminDto;
import com.margot.word_map.dto.AdminInfoDto;
import com.margot.word_map.model.Admin;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminMapper {

    private final RuleMapper ruleMapper;
    private final AdminLanguageMapper adminLanguageMapper;

    public AdminDto toDto(Admin admin) {
        return AdminDto.builder()
                .id(admin.getId())
                .email(admin.getEmail())
                .dateActive(admin.getDateActive())
                .role(admin.getRole().name().toLowerCase())
                .access(admin.isAccessGranted())
                .rules(admin.getRules().stream().map(ruleMapper::toDto).toList())
                .languages(admin.getLanguages().stream().map(adminLanguageMapper::toDto).toList())
                .build();
    }

    public AdminInfoDto toInfoDto(Admin admin) {
        return AdminInfoDto.builder()
                .email(admin.getEmail())
                .role(admin.getRole().name().toLowerCase())
                .languages(admin.getLanguages().stream()
                        .map(adminLanguageMapper::toInfoDto)
                        .toList())
                .rules(admin.getRules().stream()
                        .map(r -> r.getName().name())
                        .toList())
                .build();
    }
}
