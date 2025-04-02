package com.margot.word_map.mapper;

import com.margot.word_map.dto.LanguageDto;
import com.margot.word_map.model.Language;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LanguageMapper {

    public LanguageDto toDto(Language language) {
        return LanguageDto.builder()
                .id(language.getId())
                .prefix(language.getPrefix())
                .name(language.getName())
                .build();
    }

    public List<LanguageDto> toDto(List<Language> languages) {
        return languages.stream().map(this::toDto).toList();
    }
}
