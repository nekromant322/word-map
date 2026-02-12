package com.margot.word_map.service.map;

import com.margot.word_map.dto.LetterDto;
import com.margot.word_map.dto.request.CreateLetterRequest;
import com.margot.word_map.dto.request.UpdateLetterRequest;
import com.margot.word_map.exception.DuplicateLetterException;
import com.margot.word_map.exception.LanguageNotFoundException;
import com.margot.word_map.exception.LetterNotFoundException;
import com.margot.word_map.mapper.LetterMapper;
import com.margot.word_map.model.Language;
import com.margot.word_map.model.LetterType;
import com.margot.word_map.model.map.Letter;
import com.margot.word_map.repository.map.LetterRepository;
import com.margot.word_map.service.audit.AuditActionType;
import com.margot.word_map.service.audit.AuditService;
import com.margot.word_map.service.language.LanguageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LetterServiceTest {

    @Mock
    private LetterRepository letterRepository;

    @Mock
    private LanguageService languageService;

    @Mock
    private LetterMapper letterMapper;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private LetterService letterService;

    @Test
    public void testCreateLetterSavesAndLogs() {
        Long langId = 1L;
        Character letter = 'ы';
        String languageName = "russian";

        CreateLetterRequest request = CreateLetterRequest.builder()
                .languageId(langId)
                .letter(letter)
                .type(LetterType.VOWEL)
                .multiplier((short) 10)
                .weight((short) 1)
                .build();
        Language language = Language.builder()
                .id(langId)
                .name(languageName)
                .build();
        LetterDto expectedDto = LetterDto.builder()
                .id(1L)
                .letter(letter)
                .language(languageName)
                .build();

        when(languageService.getLanguageById(langId)).thenReturn(language);
        when(letterRepository.existsByLetterAndLanguage(letter, language)).thenReturn(false);
        when(letterMapper.toDto(any())).thenReturn(expectedDto);

        LetterDto dto = letterService.createLetter(request);

        ArgumentCaptor<Letter> captor = ArgumentCaptor.forClass(Letter.class);

        verify(letterRepository).save(captor.capture());

        Letter savedLetter = captor.getValue();
        verify(auditService).log(
                eq(AuditActionType.LANGUAGE_LETTER_CREATED),
                eq(savedLetter.getLetter()),
                eq(savedLetter.getLanguage().getId()));
        verify(letterMapper).toDto(eq(savedLetter));

        assertThat(dto).isSameAs(expectedDto);
        assertThat(savedLetter.getLetter()).isEqualTo(letter);
        assertThat(savedLetter.getLanguage()).isSameAs(language);
        assertThat(savedLetter.getType()).isEqualTo(request.getType());
        assertThat(savedLetter.getMultiplier()).isEqualTo(request.getMultiplier());
        assertThat(savedLetter.getWeight()).isEqualTo(request.getWeight());
    }

    @Test
    public void testCreateLetterThrowsWhenDuplicateLetter() {
        Long langId = 1L;
        Character letter = 'ы';

        CreateLetterRequest request = CreateLetterRequest.builder()
                .languageId(langId)
                .letter(letter)
                .build();
        Language language = Language.builder()
                .id(langId)
                .build();

        when(languageService.getLanguageById(langId)).thenReturn(language);
        when(letterRepository.existsByLetterAndLanguage(letter, language)).thenReturn(true);

        assertThatThrownBy(() -> letterService.createLetter(request))
                .isInstanceOf(DuplicateLetterException.class)
                .hasMessageStartingWith("Буква '" + letter + "' уже существует");
    }

    @Test
    public void testUpdateLetterSavesAndLogs() {
        Long letterId = 1L;
        UpdateLetterRequest request = UpdateLetterRequest.builder()
                .type(LetterType.VOWEL)
                .multiplier((short) 1)
                .weight((short) 10)
                .build();
        Language language = Language.builder()
                .id(1L)
                .build();
        Letter letter = Letter.builder()
                .id(letterId)
                .letter('ы')
                .language(language)
                .build();

        when(letterRepository.findByIdWithLanguage(letterId)).thenReturn(Optional.of(letter));

        letterService.updateLetter(letterId, request);

        verify(letterRepository).save(eq(letter));
        verify(auditService).log(eq(AuditActionType.LANGUAGE_LETTER_UPDATED), eq('ы'), eq(1L));

        assertThat(letter.getType()).isEqualTo(request.getType());
        assertThat(letter.getMultiplier()).isEqualTo(request.getMultiplier());
        assertThat(letter.getWeight()).isEqualTo(request.getWeight());
    }

    @Test
    public void testUpdateLetterThrowsWhenNoLetterFound() {
        UpdateLetterRequest request = UpdateLetterRequest.builder()
                .type(LetterType.VOWEL)
                .multiplier((short) 1)
                .weight((short) 10)
                .build();

        when(letterRepository.findByIdWithLanguage(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> letterService.updateLetter(1L, request))
                .isInstanceOf(LetterNotFoundException.class)
                .hasMessageContaining("Буква не найдена по идентификатору: 1");
    }

    @Test
    public void testDeleteLetterDeletesAndLogs() {
        Long letterId = 1L;
        Character ch = 'ы';
        String langName = "russian";

        Language language = Language.builder()
                .id(letterId)
                .name(langName)
                .build();

        Letter letter = Letter.builder()
                .id(letterId)
                .letter(ch)
                .language(language)
                .build();
        LetterDto expectedDto = LetterDto.builder()
                .id(letterId)
                .language(langName)
                .build();

        when(letterRepository.findByIdWithLanguage(letterId)).thenReturn(Optional.of(letter));
        when(letterMapper.toDto(letter)).thenReturn(expectedDto);

        LetterDto dto = letterService.deleteLetter(letterId);

        verify(letterRepository).delete(eq(letter));
        verify(auditService).log(eq(AuditActionType.LANGUAGE_LETTER_DELETED), eq(ch), eq(langName));

        assertThat(dto).isSameAs(expectedDto);
    }

    @Test
    public void testDeleteLetterThrowsWhenNoLetterFound() {
        Long letterId = 1L;

        when(letterRepository.findByIdWithLanguage(letterId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> letterService.deleteLetter(letterId))
                .isInstanceOf(LetterNotFoundException.class)
                .hasMessageContaining("Буква не найдена по идентификатору: 1");
    }

    @Test
    public void testDeleteLetterThrowsWhenSpecialSymbol() {
        Long letterId = 1L;

        Letter letter = Letter.builder()
                .id(letterId)
                .build();

        when(letterRepository.findByIdWithLanguage(letterId)).thenReturn(Optional.of(letter));

        assertThatThrownBy(() -> letterService.deleteLetter(letterId))
                .isInstanceOf(LanguageNotFoundException.class)
                .hasMessageContaining("Невозможно удалить специальный символ: 1");
    }
}

