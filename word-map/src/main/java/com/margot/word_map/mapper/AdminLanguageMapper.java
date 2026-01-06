package com.margot.word_map.mapper;

import com.margot.word_map.dto.AdminLanguageDto;
import com.margot.word_map.model.AdminLanguage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminLanguageMapper {

    public AdminLanguageDto toDto(AdminLanguage adminLanguage) {
        return AdminLanguageDto.builder()
                .languageID(adminLanguage.getLanguage().getId())
                .languagePrefix(adminLanguage.getLanguage().getPrefix())
                .build();
    }
}