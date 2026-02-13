package com.margot.word_map.service.language;

import com.margot.word_map.dto.LanguageDto;
import com.margot.word_map.dto.request.CreateUpdateLanguageRequest;
import com.margot.word_map.exception.DuplicateNameException;
import com.margot.word_map.exception.DuplicatePrefixException;
import com.margot.word_map.mapper.LanguageMapper;
import com.margot.word_map.model.Language;
import com.margot.word_map.repository.LanguageRepository;
import com.margot.word_map.service.audit.AuditActionType;
import com.margot.word_map.service.audit.AuditService;
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
public class LanguageTest {

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private LanguageMapper languageMapper;

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

        Language language = new Language(langId, "te", "test");

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
}
