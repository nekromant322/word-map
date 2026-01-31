package com.margot.word_map.repository;

import com.margot.word_map.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WordRepository extends JpaRepository<Word, Long> {

    Optional<Word> findWordByWord(String word);

    @SuppressWarnings("checkstyle:Indentation")
    @Query(value = """
        SELECT w.word FROM words w 
        WHERE w.id_language = :langId AND w.word !~ :regex  """, nativeQuery = true)
    List<String> findWordsByLanguageNotMatchingRegex(@Param("langId") Long langId, @Param("regex") String regex);
}
