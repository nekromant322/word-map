package com.margot.word_map.controller.rest;

import com.margot.word_map.controller.rest.api.PatternApi;
import com.margot.word_map.dto.PatternDto;
import com.margot.word_map.dto.request.CreateUpdatePatternRequest;
import com.margot.word_map.dto.response.IdResponse;
import com.margot.word_map.service.map.PatternService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pattern")
@RequiredArgsConstructor
public class PatternController implements PatternApi {

    private final PatternService patternService;

    @GetMapping("/{id}")
    public PatternDto getPattern(@PathVariable("id") Long id) {
        return patternService.getPatternById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public IdResponse<Long> createPattern(@Valid @RequestBody CreateUpdatePatternRequest request) {
        return patternService.createPattern(request);
    }
}
