package com.margot.word_map.controller.rest;

import com.margot.word_map.controller.rest.api.LanguageApi;
import com.margot.word_map.dto.LanguageDto;
import com.margot.word_map.dto.OptionDto;
import com.margot.word_map.dto.request.CreateUpdateLanguageRequest;
import com.margot.word_map.dto.response.LetterResponse;
import com.margot.word_map.service.language.LanguageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/language")
public class LanguageController implements LanguageApi {

    private final LanguageService languageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public LanguageDto createLanguage(@RequestBody @Valid CreateUpdateLanguageRequest request) {
        return languageService.createLanguage(request);
    }

    @PutMapping("/{id}")
    @Override
    public LanguageDto updateLanguage(
            @PathVariable Long id,
            @RequestBody @Valid CreateUpdateLanguageRequest request) {

        return languageService.updateLanguage(id, request);
    }

    @GetMapping("/list")
    @Override
    public List<LanguageDto> getLanguages() {
        return languageService.getLanguages();
    }

    @GetMapping("/option")
    @Override
    public List<OptionDto> getLanguageOptions() {
        return languageService.getLanguageOptions();
    }

    @GetMapping("/{id}/alphabet")
    @Override
    public List<LetterResponse> getAlphabet(@PathVariable("id") Long id) {
        return languageService.getAlphabet(id);
    }
}
