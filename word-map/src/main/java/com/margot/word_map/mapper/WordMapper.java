package com.margot.word_map.mapper;

import com.margot.word_map.dto.response.DictionaryWordResponse;
import com.margot.word_map.model.Word;
import org.springframework.stereotype.Component;

@Component
public class WordMapper {

    public DictionaryWordResponse toDictionaryWordResponse(Word word) {
        return DictionaryWordResponse.builder()
                .id(word.getId())
                .word(word.getWord())
                .description(word.getDescription())
                .build();
    }
}
