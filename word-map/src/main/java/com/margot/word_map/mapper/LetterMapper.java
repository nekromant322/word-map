package com.margot.word_map.mapper;

import com.margot.word_map.dto.LetterDto;
import com.margot.word_map.dto.response.LetterResponse;
import com.margot.word_map.model.map.Letter;
import org.springframework.stereotype.Component;

@Component
public class LetterMapper {

    public LetterDto toDto(Letter letter) {
        return LetterDto.builder()
                .id(letter.getId())
                .letter(letter.getLetter())
                .language(letter.getLanguage().getName())
                .build();
    }

    public LetterResponse toResponseDto(Letter letter) {
        return LetterResponse.builder()
                .id(letter.getId())
                .letter(letter.getLetter())
                .type(letter.getType())
                .multiplier(letter.getMultiplier())
                .weight(letter.getWeight())
                .build();
    }
}
