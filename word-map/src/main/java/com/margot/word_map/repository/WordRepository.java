package com.margot.word_map.repository;

import com.margot.word_map.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WordRepository extends JpaRepository<Word, Long> {

    Optional<Word> findWordByWord(String word);
}
