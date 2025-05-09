package com.margot.word_map.mapper;

import com.margot.word_map.dto.LanguageDto;
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
}
