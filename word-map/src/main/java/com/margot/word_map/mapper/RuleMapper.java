package com.margot.word_map.mapper;

import com.margot.word_map.dto.RuleDto;
import com.margot.word_map.model.Rule;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RuleMapper {

    public RuleDto toDto(Rule rule) {
        return RuleDto.builder()
                .id(rule.getId())
                .rule(rule.getName().name())
                .build();
    }
}
