package com.margot.word_map.mapper;

import com.margot.word_map.dto.RuleDto;
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
}
