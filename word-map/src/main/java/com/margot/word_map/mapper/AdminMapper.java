package com.margot.word_map.mapper;

import com.margot.word_map.dto.AdminDto;
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
                .dateCreation(admin.getDateCreation())
                .dateActive(admin.getDateActive())
                .role(admin.getRole().name())
                .access(admin.getAccess())
                .adminRules(admin.getRules().stream().map(ruleMapper::toDto).toList())
                .languages(admin.getLanguages().stream().map(adminLanguageMapper::toDto).toList())
                .build();
    }
}
