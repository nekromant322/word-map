package com.margot.word_map.mapper;

import com.margot.word_map.dto.LanguageDto;
import com.margot.word_map.dto.AdminLanguageDto;
import com.margot.word_map.dto.OptionDto;
import com.margot.word_map.model.Language;
import org.springframework.stereotype.Component;

@Component
public class LanguageMapper {

    public LanguageDto toDto(Language language) {
        return LanguageDto.builder()
                .id(language.getId())
                .prefix(language.getPrefix())
                .name(language.getName())
                .build();
    }

    public AdminLanguageDto toInfoDto(Language language) {
        return AdminLanguageDto.builder()
                .languageID(language.getId())
                .languagePrefix(language.getPrefix())
                .build();
    }

    public OptionDto toOptionDto(Language language) {
        return OptionDto.builder()
                .value(String.valueOf(language.getId()))
                .label(language.getPrefix())
                .description(language.getName())
                .build();
    }
}
