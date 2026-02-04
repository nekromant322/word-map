package com.margot.word_map.repository.map;

import com.margot.word_map.model.map.Letter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LetterRepository extends JpaRepository<Letter, Long> {

    List<Letter> findAllByLanguageId(Long languageId);
}
