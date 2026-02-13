package com.margot.word_map.service.word;

import com.margot.word_map.dto.request.DictionaryListRequest;
import com.margot.word_map.dto.request.SymbolPosition;
import com.margot.word_map.dto.response.DictionaryListResponse;
import com.margot.word_map.exception.FormatErrorException;
import com.margot.word_map.model.Language;
import com.margot.word_map.model.Word;
import com.margot.word_map.repository.WordRepository;
import com.margot.word_map.service.language.LanguageService;
import com.margot.word_map.service.map.LetterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WordServiceTest {
    @Mock
    private WordRepository wordRepository;

    @Mock
    private LetterService letterService;

    @Mock
    private LanguageService languageService;

    @InjectMocks
    private WordService wordService;

    @Test
    void testThrowExceptionWhenLanguageIdNotFound() {
        DictionaryListRequest request = getDictionaryListRequestNonUniqueLetters();
        when(languageService.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> wordService
                .getWordsByFilters(request))
                .isInstanceOf(FormatErrorException.class)
                .hasMessage("Нет такого языка");
    }

    @Test
    void testFilterByLettersWhenReuseTrue() {
        DictionaryListRequest request = getDictionaryListRequestNonUniqueLetters();
        Set<Character> alphabet = Set.of('а', 'п', 'р', 'о', 'д', 'у', 'ш', 'т');
        Word word = Word.builder()
                .word("Апорт")
                .build();

        when(languageService.findById(1L)).thenReturn(Optional.of(new Language()));
        when(wordRepository.findAll(any(Specification.class))).thenReturn(List.of(word));
        when(letterService.getAllowedLetters(1L)).thenReturn(alphabet);
        when(letterService.validateAlphabet(anyString(), anySet())).thenReturn(true);

        DictionaryListResponse response = wordService.getWordsByFilters(request);
        assertThat(response.getWord()).containsExactly("Апорт");
        verify(wordRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void testFilterByLettersWhenReuseFalse() {
        DictionaryListRequest request = getDictionaryListRequestUniqueLetters();
        Set<Character> alphabet = Set.of('а', 'п', 'р', 'о', 'д', 'у', 'ш', 'т');
        String expectedRegex = "[апр]";

        when(languageService.findById(1L)).thenReturn(Optional.of(new Language()));
        when(wordRepository.findWordsByLetters(eq(1L), eq(expectedRegex), eq("апр")))
                .thenReturn(List.of("Апорт"));
        when(letterService.getAllowedLetters(1L)).thenReturn(alphabet);
        when(letterService.validateAlphabet(anyString(), anySet())).thenReturn(true);

        DictionaryListResponse response = wordService.getWordsByFilters(request);
        assertThat(response.getWord()).containsExactly("Апорт");
        verify(wordRepository, times(1)).findWordsByLetters(1L, expectedRegex, "апр");
    }

    @Test
    void TestThrowExceptionWhenLettersUsedContainsInvalidCharacter() {
        DictionaryListRequest request = DictionaryListRequest.builder()
                .languageId(1L)
                .lettersUsed("test")
                .reuse(true)
                .build();
        Set<Character> alphabet = Set.of('а', 'п', 'р', 'о', 'д', 'у', 'ш', 'т');
        when(languageService.findById(1L)).thenReturn(Optional.of(new Language()));
        when(letterService.getAllowedLetters(1L)).thenReturn(alphabet);
        when(letterService.validateAlphabet(anyString(), anySet())).thenReturn(false);

        assertThatThrownBy(() -> wordService.getWordsByFilters(request))
                .isInstanceOf(FormatErrorException.class)
                .hasMessage("'lettersUsed' содержит некорректные символы");
        verifyNoInteractions(wordRepository);
    }

    @Test
    void TestThrowExceptionWhenLettersExcludeContainsInvalidCharacter() {
        DictionaryListRequest request = DictionaryListRequest.builder()
                .languageId(1L)
                .lettersExclude("test")
                .reuse(true)
                .build();
        Set<Character> alphabet = Set.of('а', 'п', 'р', 'о', 'д', 'у', 'ш', 'т');
        when(languageService.findById(1L)).thenReturn(Optional.of(new Language()));
        when(letterService.getAllowedLetters(1L)).thenReturn(alphabet);
        when(letterService.validateAlphabet(anyString(), anySet())).thenReturn(false);

        assertThatThrownBy(() -> wordService.getWordsByFilters(request))
                .isInstanceOf(FormatErrorException.class)
                .hasMessage("'lettersExclude' содержит некорректные символы");
        verifyNoInteractions(wordRepository);
    }

    @Test
    void TestThrowExceptionWhenLettersContainsInvalidCharacter() {
        DictionaryListRequest request = DictionaryListRequest.builder()
                .languageId(1L)
                .positions(List.of(new SymbolPosition(5, 't')))
                .reuse(true)
                .build();
        Set<Character> alphabet = Set.of('а', 'п', 'р', 'о', 'д', 'у', 'ш', 'т');
        when(languageService.findById(1L)).thenReturn(Optional.of(new Language()));
        when(letterService.getAllowedLetters(1L)).thenReturn(alphabet);
        when(letterService.validateAlphabet(anyString(), anySet())).thenReturn(false);

        assertThatThrownBy(() -> wordService.getWordsByFilters(request))
                .isInstanceOf(FormatErrorException.class)
                .hasMessage("'letter' содержит некорректные символы");
        verifyNoInteractions(wordRepository);
    }

    @Test
    void testEmptyResponse() {
        DictionaryListRequest request = getDictionaryListRequestNonUniqueLetters();
        when(languageService.findById(1L)).thenReturn(Optional.of(new Language()));
        when(letterService.getAllowedLetters(1L)).thenReturn(Set.of('а', 'п', 'р', 'о', 'д', 'у', 'ш', 'т'));
        when(letterService.validateAlphabet(anyString(), anySet())).thenReturn(true);
        when(wordRepository.findAll(any(Specification.class))).thenReturn(List.of());

        DictionaryListResponse response = wordService.getWordsByFilters(request);
        assertThat(response.getWord()).isEmpty();
    }

    @Test
    void testResponseMappingMultipleWords() {
        DictionaryListRequest request = getDictionaryListRequestNonUniqueLetters();
        when(languageService.findById(1L)).thenReturn(Optional.of(new Language()));
        when(letterService.getAllowedLetters(anyLong())).thenReturn(Set.of('а', 'п', 'р', 'о', 'д', 'у', 'ш', 'т'));
        when(letterService.validateAlphabet(anyString(), anySet())).thenReturn(true);
        when(wordRepository.findAll(any(Specification.class))).thenReturn(List.of(
                Word.builder().word("Апорт").build(),
                Word.builder().word("Пар").build()
        ));

        DictionaryListResponse response = wordService.getWordsByFilters(request);
        assertThat(response.getWord()).containsExactly("Апорт", "Пар");
    }

    private DictionaryListRequest getDictionaryListRequestNonUniqueLetters() {
        List<SymbolPosition> position = new ArrayList<>();
        position.add(new SymbolPosition(0, 'а'));
        return DictionaryListRequest.builder()
                .languageId(1L)
                .reuse(true)
                .lettersUsed("апродушт")
                .lettersExclude("лйч")
                .wordLength(5)
                .positions(position)
                .build();
    }

    private DictionaryListRequest getDictionaryListRequestUniqueLetters() {
        return DictionaryListRequest.builder()
                .languageId(1L)
                .lettersUsed("апр")
                .reuse(false)
                .build();
    }
}
