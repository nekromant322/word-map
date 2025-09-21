package com.margot.word_map.repository.map;

import com.margot.word_map.model.map.Letter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LetterRepository extends JpaRepository<Letter, Short> {

    Letter findByLetter(String letter);
}
