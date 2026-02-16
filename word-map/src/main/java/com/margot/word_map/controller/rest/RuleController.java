package com.margot.word_map.controller.rest;

import com.margot.word_map.controller.rest.api.RuleApi;
import com.margot.word_map.dto.OptionDto;
import com.margot.word_map.service.rule.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rule")
public class RuleController implements RuleApi {

    private final RuleService ruleService;

    @GetMapping("/option")
    @Override
    public List<OptionDto> getRules() {
        return ruleService.getRuleOptions();
    }
}
