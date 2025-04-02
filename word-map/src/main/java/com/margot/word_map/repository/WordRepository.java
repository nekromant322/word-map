package com.margot.word_map.repository;

import com.margot.word_map.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.stream.Stream;

public interface WordRepository extends JpaRepository<Word, Long> {

    Optional<Word> getWordByWord(String word);

    Optional<Word> getWordById(Long id);
}
