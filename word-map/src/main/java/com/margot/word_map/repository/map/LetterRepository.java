package com.margot.word_map.repository.map;

import com.margot.word_map.model.Language;
import com.margot.word_map.model.map.Letter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LetterRepository extends JpaRepository<Letter, Long> {

    List<Letter> findAllByLanguageId(Long languageId);

    @Query("""
            SELECT COUNT(l) > 0
            FROM Letter l
            WHERE LOWER(l.letter) = LOWER(:letter)
                AND l.language = :language
            """)
    boolean existsByLetterAndLanguage(Character letter, Language language);

    @Query("""
            SELECT l FROM Letter l
            JOIN FETCH l.language
            WHERE l.id = :id
            """)
    Optional<Letter> findByIdWithLanguage(@Param("id") Long id);
}
