package com.margot.word_map.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.margot.word_map.dto.request.CreateWordRequest;
import com.margot.word_map.dto.request.UpdateWordRequest;
import com.margot.word_map.dto.response.DictionaryWordResponse;
import com.margot.word_map.exception.WordAlreadyExists;
import com.margot.word_map.exception.WordNotFoundException;
import com.margot.word_map.mapper.WordMapper;
import com.margot.word_map.model.Word;
import com.margot.word_map.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;

    private final WordMapper wordMapper;

    public DictionaryWordResponse getWordInfo(String word) {
        return wordMapper.toDictionaryWordResponse(wordRepository.getWordByWord(word).orElseThrow(() -> {
            log.info("word not found {}", word);
            return new WordNotFoundException("word " + word + " not found");
        }));
    }

    public void createNewWord(CreateWordRequest request) {
        // получить админа с контекста авторизации и присунуть в word

        wordRepository.getWordByWord(request.getWord()).ifPresentOrElse(
                (word) -> {
                    log.info("word {} already exists", request.getWord());
                    throw new WordAlreadyExists("word " + word.getWord() + " already exists");
                },
                () -> {
                    Word word = Word.builder()
                            .word(request.getWord())
                            .description(request.getDescription())
                            .wordLength(request.getWord().length())
                            .dateCreation(LocalDateTime.now())
                            .build();

                    wordRepository.save(word);
                }
        );
    }

    @Transactional
    public void updateWordInfo(UpdateWordRequest request) {
        // получить админа с контекста и засунуть в editedBy
        Optional<Word> wordByNameOp = wordRepository.getWordByWord(request.getWord());
        if (wordByNameOp.isPresent() && !wordByNameOp.get().getId().equals(request.getId())) {
            log.info("word {} already exists", request.getWord());
            throw new WordAlreadyExists("word " + request.getWord() + " already exists with another id");
        }

        Word wordToUpdate = wordByNameOp.orElseGet(() -> {
            Optional<Word> wordById = wordRepository.findById(request.getId());
            return wordById.orElseThrow(() -> {
                log.info("word with id {} not found", request.getId());
                return new WordNotFoundException("word with id " + request.getId() + " not found");
            });
        });

        wordToUpdate.setWord(request.getWord());
        wordToUpdate.setDescription(request.getDescription());
        wordToUpdate.setWordLength(request.getWord().length());
        wordToUpdate.setDateEdited(LocalDateTime.now());
        // добавить админа которым сделана замена
        wordRepository.save(wordToUpdate);
    }

    public void deleteWord(Long id) {
        // получить пользователя с контекста

        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new WordNotFoundException("Word with id " + id + " not found"));

        wordRepository.delete(word);
        // здесь почту пользователя или id добавить
        log.info("DELETE WORD Пользователь {} удалил слово {}.", id, word.getWord());
    }

    public StreamingResponseBody getAllWords() {
        return outputStream -> {
            try (JsonGenerator generator = new ObjectMapper().getFactory().createGenerator(outputStream)) {
                generator.writeStartArray();
                wordRepository.findAll().stream()
                        .map(w -> new DictionaryWordResponse(w.getId(), w.getWord(), w.getDescription()))
                        .forEach(word -> {
                            try {
                                generator.writeObject(word);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                generator.writeEndArray();
            }
        };
    }
}
