package com.margot.word_map.mapper;

import com.margot.word_map.dto.RuleDto;
import com.margot.word_map.dto.OptionDto;
import com.margot.word_map.model.Rule;
import org.springframework.stereotype.Component;

@Component
public class RuleMapper {

    public RuleDto toDto(Rule rule) {
        return RuleDto.builder()
                .ruleID(rule.getId())
                .ruleName(rule.getName().name())
                .build();
    }

    public OptionDto toOptionDto(Rule rule) {
        return OptionDto.builder()
                .label(rule.getName().name())
                .value(rule.getId().toString())
                .description("")
                .build();
    }
}
