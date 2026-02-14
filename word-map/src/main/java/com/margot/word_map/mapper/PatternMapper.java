package com.margot.word_map.mapper;

import com.margot.word_map.dto.PatternDto;
import com.margot.word_map.model.Pattern;
import org.springframework.stereotype.Component;

@Component
public class PatternMapper {

    public PatternDto toDto(Pattern pattern) {
        return PatternDto.builder()
                .id(pattern.getId())
                .cells(pattern.getCells())
                .patternType(pattern.getPatternType())
                .isDraft(pattern.getIsDraft())
                .weight(pattern.getWeight())
                .createdAt(pattern.getCreatedAt())
                .build();
    }
}
