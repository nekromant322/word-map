package com.margot.word_map.service.map;

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

    public boolean validateAlphabet(String word, Set<Character> allowedLetters) {
        String lowerCaseWord = word.toLowerCase();

        for (char c : lowerCaseWord.toCharArray()) {
            if (!allowedLetters.contains(c)) {
                return false;
            }
        }
        return true;
    }

    public Set<Character> getAllowedLetters(Long languageId) {
        return letterRepository.findAllByLanguageId(languageId)
                .stream()
                .map(Letter::getLetter)
                .map(Character::toLowerCase)
                .collect(Collectors.toSet());
    }
}

