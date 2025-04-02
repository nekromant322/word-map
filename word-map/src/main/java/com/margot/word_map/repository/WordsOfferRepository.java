package com.margot.word_map.repository;

import com.margot.word_map.model.WordOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordsOfferRepository extends JpaRepository<WordOffer, Long> {

    WordOffer findByWord(String word);

    Page<WordOffer> findAllByCheckedIsFalse (Pageable pageable);
}
