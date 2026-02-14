package com.margot.word_map.controller.rest;

import com.margot.word_map.controller.rest.api.PatternApi;
import com.margot.word_map.dto.PatternDto;
import com.margot.word_map.service.map.PatternService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pattern")
@RequiredArgsConstructor
public class PatternController implements PatternApi {

    private final PatternService patternService;

    @GetMapping("/{id}")
    public PatternDto getPattern(@PathVariable("id") Long id) {
        return patternService.getPatternById(id);
    }
}
