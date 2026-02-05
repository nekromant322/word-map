package com.margot.word_map.service.map;

import com.margot.word_map.dto.request.CreateWordRequest;
import com.margot.word_map.model.map.Letter;
import com.margot.word_map.repository.map.LetterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LetterService {

    private final LetterRepository letterRepository;

    public boolean validateAlphabet(CreateWordRequest request) {
        Set<Character> allowedLetters = letterRepository.findAllByLanguageId(request.getLanguageId())
                .stream()
                .map(Letter::getLetter)
                .map(Character::toUpperCase)
                .collect(Collectors.toSet());

        String word = request.getWord().toUpperCase();

        for (char c : word.toCharArray()) {
            if (!allowedLetters.contains(c)) {
                return false;
            }
        }
        return true;
    }
}
