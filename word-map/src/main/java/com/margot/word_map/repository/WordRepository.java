package com.margot.word_map.repository;

import com.margot.word_map.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long>, JpaSpecificationExecutor<Word> {

    Optional<Word> findWordByWord(String word);

    List<Word> findAllByLanguageId(Long languageId);

    Optional<Word> findWordByWordAndLanguageId(String word, Long languageId);

    @SuppressWarnings("checkstyle:Indentation")
    @Query("""
                SELECT w.word 
                FROM Word w 
                WHERE w.language.id = :languageId 
                  AND LENGTH(w.word) >= LENGTH(:lettersUsed) 
                  AND function('regexp_like', w.word, :regex) = true
            """)
    List<String> findWordsByLetters(
            @Param("languageId") Long languageId,
            @Param("regex") String regex,
            @Param("lettersUsed") String lettersUsed);
}
