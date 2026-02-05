package com.margot.word_map.service.rule;

import com.margot.word_map.dto.OptionDto;
import com.margot.word_map.dto.RuleDto;
import com.margot.word_map.exception.InvalidRuleException;
import com.margot.word_map.mapper.RuleMapper;
import com.margot.word_map.model.Rule;
import com.margot.word_map.repository.RuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RuleService {

    private final RuleRepository ruleRepository;

    private final RuleMapper ruleMapper;

    public List<RuleDto> getRulesDto() {
        return ruleRepository.findAll().stream().map(ruleMapper::toDto).toList();
    }

    public List<Rule> getRules() { return ruleRepository.findAll(); }

    public Optional<Rule> getRuleByRule(Rule.RULE rule) {
        return ruleRepository.findRuleByName(rule);
    }

    public Set<Rule> getRulesByIds(List<Long> ids) {
        Set<Rule> rules = ruleRepository.findAllByIds(ids);

        if (rules.size() != ids.size()) {
            throw new InvalidRuleException("wrong rule provided");
        }
        return rules;
    }

    public List<OptionDto> getRuleOptions() {
        return ruleRepository.findAll().stream().map(ruleMapper::toOptionDto).toList();
    }
}
