package com.margot.word_map.controller.rest;

import com.margot.word_map.controller.rest.api.PatternApi;
import com.margot.word_map.dto.PatternDto;
import com.margot.word_map.dto.request.CreateUpdatePatternRequest;
import com.margot.word_map.dto.request.PatternSearchRequest;
import com.margot.word_map.dto.response.IdResponse;
import com.margot.word_map.dto.response.PagedResponseDto;
import com.margot.word_map.service.map.PatternService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @PutMapping("/{id}")
    public IdResponse<Long> updatePattern(
            @PathVariable("id") Long id,
            @Valid @RequestBody CreateUpdatePatternRequest request) {
        return patternService.updatePattern(id, request);
    }

    @DeleteMapping("/{id}")
    public IdResponse<Long> deletePattern(@PathVariable("id") Long id) {
        return patternService.deletePattern(id);
    }

    @PostMapping("/list")
    public PagedResponseDto<PatternDto> getPatternsByFilter(
            @PageableDefault(size = 20) Pageable pageable,
            @Valid @RequestBody PatternSearchRequest request) {
        return patternService.getPatternsByFilter(pageable, request);
    }
}
