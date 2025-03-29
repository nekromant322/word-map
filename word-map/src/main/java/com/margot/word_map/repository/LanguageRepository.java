package com.margot.word_map.repository;

import com.margot.word_map.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language, Long> {

    Optional<Language> getLanguageByPrefix(String prefix);
}
