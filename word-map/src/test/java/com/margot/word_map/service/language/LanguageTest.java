package com.margot.word_map.service.language;

import com.margot.word_map.dto.LanguageDto;
import com.margot.word_map.dto.request.CreateUpdateLanguageRequest;
import com.margot.word_map.dto.response.LetterResponse;
import com.margot.word_map.exception.DuplicateNameException;
import com.margot.word_map.exception.DuplicatePrefixException;
import com.margot.word_map.mapper.LanguageMapper;
import com.margot.word_map.mapper.LetterMapper;
import com.margot.word_map.model.Language;
import com.margot.word_map.model.map.Letter;
import com.margot.word_map.repository.LanguageRepository;
import com.margot.word_map.service.audit.AuditActionType;
import com.margot.word_map.service.audit.AuditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LanguageTest {

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private LanguageMapper languageMapper;

    @Mock
    private LetterMapper letterMapper;

    @InjectMocks
    private LanguageService languageService;

    @Test
    public void testCreateLanguageSavesAndLogs() {
        String name = "russian";
        String prefix = "ru";

        CreateUpdateLanguageRequest request = CreateUpdateLanguageRequest.builder()
                .name(name)
                .prefix(prefix)
                .build();

        LanguageDto expectedDto = LanguageDto.builder()
                .id(1L)
                .name(name)
                .prefix(prefix)
                .build();

        when(languageRepository.existsByPrefixExcludeId(request.getPrefix(), null)).thenReturn(false);
        when(languageRepository.existsByNameExcludeId(request.getName(), null)).thenReturn(false);
        when(languageMapper.toDto(any())).thenReturn(expectedDto);

        LanguageDto dto = languageService.createLanguage(request);

        ArgumentCaptor<Language> langCaptor = ArgumentCaptor.forClass(Language.class);
        verify(languageRepository).save(langCaptor.capture());

        Language lang = langCaptor.getValue();

        verify(auditService).log(eq(AuditActionType.LANGUAGE_CREATED), eq(lang.getName()));
        assertThat(lang.getName()).isEqualTo(request.getName());
        assertThat(lang.getPrefix()).isEqualTo(request.getPrefix());
        assertThat(dto).isSameAs(expectedDto);
    }

    @Test
    public void testCreateLanguageThrowsWhenDuplicateName() {
        CreateUpdateLanguageRequest request = CreateUpdateLanguageRequest.builder()
                .name("russian")
                .prefix("ru")
                .build();

        when(languageRepository.existsByPrefixExcludeId(request.getPrefix(), null)).thenReturn(false);
        when(languageRepository.existsByNameExcludeId(request.getName(), null)).thenReturn(true);

        assertThatThrownBy(() -> languageService.createLanguage(request))
                .isInstanceOf(DuplicateNameException.class)
                .hasMessageContaining("Язык с данным именем уже существует: " + request.getName());
    }

    @Test
    public void testCreateLanguageThrowsWhenDuplicatePrefix() {
        CreateUpdateLanguageRequest request = CreateUpdateLanguageRequest.builder()
                .name("russian")
                .prefix("ru")
                .build();

        when(languageRepository.existsByPrefixExcludeId(request.getPrefix(), null)).thenReturn(true);

        assertThatThrownBy(() -> languageService.createLanguage(request))
                .isInstanceOf(DuplicatePrefixException.class)
                .hasMessageContaining("Язык с данным префиксом уже существует: " + request.getPrefix());
    }

    @Test
    public void testUpdateLanguageSavesAndLogs() {
        Long langId = 1L;
        String name = "russian";
        String prefix = "ru";

        CreateUpdateLanguageRequest request = CreateUpdateLanguageRequest.builder()
                .name(name)
                .prefix(prefix)
                .build();

        LanguageDto expectedDto = LanguageDto.builder()
                .id(1L)
                .name(name)
                .prefix(prefix)
                .build();

        Language language = Language.builder()
                .id(langId)
                .prefix("te")
                .name("test")
                .build();

        when(languageRepository.findById(1L)).thenReturn(Optional.of(language));
        when(languageRepository.existsByPrefixExcludeId(request.getPrefix(), langId)).thenReturn(false);
        when(languageRepository.existsByNameExcludeId(request.getName(), langId)).thenReturn(false);
        when(languageMapper.toDto(any())).thenReturn(expectedDto);

        LanguageDto dto = languageService.updateLanguage(langId, request);

        ArgumentCaptor<Language> langCaptor = ArgumentCaptor.forClass(Language.class);

        verify(languageRepository).save(langCaptor.capture());
        verify(auditService).log(eq(AuditActionType.LANGUAGE_UPDATED), eq(language.getName()));
        verify(languageMapper).toDto(eq(language));

        assertThat(langCaptor.getValue()).isSameAs(language);
        assertThat(dto).isSameAs(expectedDto);
        assertThat(language.getPrefix()).isEqualTo(request.getPrefix());
        assertThat(language.getName()).isEqualTo(request.getName());
    }

    @Test
    public void testUpdateLanguageThrowsWhenDuplicateName() {
        Long id = 1L;
        CreateUpdateLanguageRequest request = CreateUpdateLanguageRequest.builder()
                .name("russian")
                .prefix("ru")
                .build();

        when(languageRepository.existsByPrefixExcludeId(request.getPrefix(), id)).thenReturn(false);
        when(languageRepository.existsByNameExcludeId(request.getName(), id)).thenReturn(true);

        assertThatThrownBy(() -> languageService.updateLanguage(id, request))
                .isInstanceOf(DuplicateNameException.class)
                .hasMessageContaining("Язык с данным именем уже существует: " + request.getName());
    }

    @Test
    void testGetAlphabet() {
        Long langId = 1L;
        Letter letter1 = Letter.builder()
                .id(1L)
                .letter('A')
                .build();
        Letter letter2 = Letter.builder()
                .id(2L)
                .letter('N')
                .build();
        Language lang = Language.builder()
                .id(langId)
                .letters(List.of(letter1, letter2))
                .build();

        LetterResponse resp1 = LetterResponse.builder()
                .id(1L)
                .letter('A')
                .build();
        LetterResponse resp2 = LetterResponse.builder()
                .id(2L)
                .letter('B')
                .build();

        when(languageRepository.findByIdWithLetters(langId)).thenReturn(Optional.of(lang));
        when(letterMapper.toResponseDto(letter1)).thenReturn(resp1);
        when(letterMapper.toResponseDto(letter2)).thenReturn(resp2);

        List<LetterResponse> result = languageService.getAlphabet(langId);

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().getLetter()).isEqualTo('A');
        verify(languageRepository).findByIdWithLetters(langId);
        verify(letterMapper, times(2)).toResponseDto(any(Letter.class));
    }

    @Test
    void testGetAlphabetEmptyWhenLanguageNotFound() {
        Long langId = 99L;
        when(languageRepository.findByIdWithLetters(langId)).thenReturn(Optional.empty());

        List<LetterResponse> result = languageService.getAlphabet(langId);

        assertThat(result).isEmpty();
        verifyNoInteractions(letterMapper);

    }
}
