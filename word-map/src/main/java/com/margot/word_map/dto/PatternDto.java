package com.margot.word_map.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.margot.word_map.model.PatternCell;
import com.margot.word_map.model.enums.PatternType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatternDto {

    private Long id;

    private List<PatternCell> cells;

    private Boolean isDraft;

    private PatternType patternType;

    private Short weight;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime createdAt;
}
