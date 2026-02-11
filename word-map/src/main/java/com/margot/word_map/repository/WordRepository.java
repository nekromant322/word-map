package com.margot.word_map.repository;

import com.margot.word_map.model.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long>, WordRepositoryCustom {

    Optional<Word> findWordByWord(String word);

    Page<Word> findAllByLanguageId(Long languageId, PageRequest pageRequest);

    Optional<Word> findWordByWordAndLanguageId(String word, Long languageId);

    @SuppressWarnings("checkstyle:Indentation")
    @Query(value = """
    SELECT word
    FROM words
    WHERE language_id = :languageId
      AND word_length >= LENGTH(:lettersUsed)
      AND word ~ :regex
            """, nativeQuery = true)
    List<String> findWordsByLetters(
            @Param("languageId") Long languageId,
            @Param("regex") String regex,
            @Param("lettersUsed") String lettersUsed);

    List<String> findWordsByLanguageId(Long languageId);
}
