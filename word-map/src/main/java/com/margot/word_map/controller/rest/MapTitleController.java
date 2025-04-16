package com.margot.word_map.controller.rest;

import com.margot.word_map.dto.request.LettersWithCoordinatesRequest;
import com.margot.word_map.service.WordService;
import com.margot.word_map.service.map.MapTitlesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
public class MapTitleController {

    private final MapTitlesService mapTitlesService;
    private final WordService wordService;

    @PostMapping("/sendWord")
    public void sendWord (@RequestBody LettersWithCoordinatesRequest lettersWithCoordinatesRequest) {
        wordService.getWordInfo(lettersWithCoordinatesRequest.getWord());
        mapTitlesService.check(lettersWithCoordinatesRequest);
        mapTitlesService.save(lettersWithCoordinatesRequest);
    }
}
