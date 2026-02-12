package com.margot.word_map.service.map;

import com.margot.word_map.dto.LetterDto;
import com.margot.word_map.dto.request.CreateLetterRequest;
import com.margot.word_map.dto.request.UpdateLetterRequest;
import com.margot.word_map.exception.DuplicateLetterException;
import com.margot.word_map.exception.LetterNotFoundException;
import com.margot.word_map.mapper.LetterMapper;
import com.margot.word_map.model.Language;
import com.margot.word_map.model.map.Letter;
import com.margot.word_map.repository.map.LetterRepository;
import com.margot.word_map.service.audit.AuditActionType;
import com.margot.word_map.service.audit.AuditService;
import com.margot.word_map.service.language.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LetterService {

    private final LetterRepository letterRepository;
    private final LanguageService languageService;
    private final LetterMapper letterMapper;
    private final AuditService auditService;

    @Transactional
    public LetterDto createLetter(CreateLetterRequest request) {
        Language language = languageService.getLanguageById(request.getLanguageId());

        if (letterRepository.existsByLetterAndLanguage(request.getLetter(), language)) {
            throw new DuplicateLetterException(
                    String.format(
                            "Буква '%s' уже существует для языка %s",
                            request.getLetter(),
                            language.getName()));
        }

        Letter letter = Letter.builder()
                .language(language)
                .letter(request.getLetter())
                .type(request.getType())
                .multiplier(request.getMultiplier())
                .weight(request.getWeight())
                .build();

        letterRepository.save(letter);
        auditService.log(AuditActionType.LANGUAGE_LETTER_CREATED, request.getLetter(), request.getLanguageId());

        return letterMapper.toDto(letter);
    }

    @Transactional
    public LetterDto updateLetter(Long letterId, UpdateLetterRequest request) {
        Letter letter = letterRepository.findByIdWithLanguage(letterId)
                .orElseThrow(() -> new LetterNotFoundException("Буква не найдена по идентификатору: " + letterId));

        Optional.ofNullable(request.getType()).ifPresent(letter::setType);
        Optional.ofNullable(request.getMultiplier()).ifPresent(letter::setMultiplier);
        Optional.ofNullable(request.getWeight()).ifPresent(letter::setWeight);

        letterRepository.save(letter);
        auditService.log(AuditActionType.LANGUAGE_LETTER_UPDATED, letter.getLetter(), letter.getLanguage().getId());

        return letterMapper.toDto(letter);
    }

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
