package com.margot.word_map.service;

import com.margot.word_map.dto.RuleDto;
import com.margot.word_map.mapper.RuleMapper;
import com.margot.word_map.model.Rule;
import com.margot.word_map.repository.RuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RuleService {

    private final RuleRepository ruleRepository;

    private final RuleMapper ruleMapper;

    public List<RuleDto> getRulesDto() {
        return ruleMapper.toDto(ruleRepository.findAll());
    }

    public List<Rule> getRules() { return ruleRepository.findAll(); }

    public Optional<Rule> getRuleByRule(Rule.RULE rule) {
        return ruleRepository.findRuleByAuthority(rule);
    }
}
