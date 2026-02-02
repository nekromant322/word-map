package com.margot.word_map.mapper;

import com.margot.word_map.dto.response.DictionaryDetailedWordResponse;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.Word;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WordMapper {

    public DictionaryDetailedWordResponse toDictionaryDetailedWordResponse(Word word) {
        return DictionaryDetailedWordResponse.builder()
                .id(word.getId())
                .word(word.getWord())
                .description(word.getDescription())
                .length(word.getWordLength())
                .createdAt(word.getCreatedAt())
                .creatorEmail(word.getCreatedBy().getEmail())
                .editedAt(word.getEditedAt())
                .editorEmail(Optional.ofNullable(word.getEditedBy()).map(Admin::getEmail).orElse(null))
                .build();
    }
}
