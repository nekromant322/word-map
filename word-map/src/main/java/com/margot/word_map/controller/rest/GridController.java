package com.margot.word_map.controller.rest;

import com.margot.word_map.dto.request.WordAndLettersWithCoordinates;
import com.margot.word_map.model.User;
import com.margot.word_map.service.map.GridService;
import com.margot.word_map.service.word.WordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
@Validated
public class GridController {

    private final GridService gridService;
    private final WordService wordService;

    @PostMapping("/word")
    public void sendWord (@Valid @RequestBody WordAndLettersWithCoordinates word, UserDetails userDetails) {
        checkAndSave(word, userDetails);
    }

    private void checkAndSave(WordAndLettersWithCoordinates word, UserDetails userDetails) {
        User user = (User) userDetails;
        wordService.getWordInfo(word.getWord());
        gridService.check(word);
        gridService.save(word, user);
    }
}
