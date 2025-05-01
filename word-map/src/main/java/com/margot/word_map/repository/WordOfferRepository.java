package com.margot.word_map.repository;

import com.margot.word_map.model.WordOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WordOfferRepository extends JpaRepository<WordOffer, Long>, JpaSpecificationExecutor<WordOffer> {

    Optional<WordOffer> findByWord(String word);

}
