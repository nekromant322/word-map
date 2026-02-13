package com.margot.word_map.repository;

import com.margot.word_map.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language, Long> {

    Optional<Language> getLanguageByPrefix(String prefix);

    Optional<Language> findByName(String name);

    @Query("""
            SELECT l FROM Language l
            JOIN FETCH l.letters
            WHERE l.id = :id
            """)
    Optional<Language> findByIdWithLetters(Long id);

    @Query("""
            SELECT COUNT(l) > 0 FROM Language l
            WHERE l.prefix = :prefix
            AND (:excludeId IS NULL OR l.id != :excludeId)
            """)
    boolean existsByPrefixExcludeId(String prefix, Long excludeId);

    @Query("""
            SELECT COUNT(l) > 0 FROM Language l
            WHERE l.name = :name
            AND (:excludeId IS NULL OR l.id != :excludeId)
            """)
    boolean existsByNameExcludeId(String name, Long excludeId);
}
