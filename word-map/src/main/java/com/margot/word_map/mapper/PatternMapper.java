package com.margot.word_map.mapper;

import com.margot.word_map.dto.PatternDto;
import com.margot.word_map.dto.request.CreateUpdatePatternRequest;
import com.margot.word_map.model.Pattern;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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

    public Pattern toEntity(CreateUpdatePatternRequest request) {
        return Pattern.builder()
                .patternType(request.getPatternType())
                .isDraft(request.getIsDraft())
                .weight(request.getWeight())
                .cells(request.getCells())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
