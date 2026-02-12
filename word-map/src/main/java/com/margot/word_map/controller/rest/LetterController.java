package com.margot.word_map.controller.rest;

import com.margot.word_map.controller.rest.api.LetterApi;
import com.margot.word_map.dto.LetterDto;
import com.margot.word_map.dto.request.CreateLetterRequest;
import com.margot.word_map.dto.request.UpdateLetterRequest;
import com.margot.word_map.service.map.LetterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/letter")
@RequiredArgsConstructor
public class LetterController implements LetterApi {

    private final LetterService letterService;

    @PostMapping
    public LetterDto createLetter(@RequestBody @Valid CreateLetterRequest request) {
        return letterService.createLetter(request);
    }

    @PutMapping("/{id}")
    public LetterDto updateLetter(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateLetterRequest request) {
        return letterService.updateLetter(id, request);
    }

    @DeleteMapping("/{id}")
    public LetterDto deleteLetter(@PathVariable("id") Long id) {
        return letterService.deleteLetter(id);
    }
}
