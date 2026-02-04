package com.margot.word_map.repository;

import com.margot.word_map.model.WordOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WordOfferRepository extends JpaRepository<WordOffer, Long>, JpaSpecificationExecutor<WordOffer> {

    Optional<WordOffer> findByWord(String word);

    @Modifying
    @Query("""
        UPDATE WordOffer o
        SET o.status = 'approved'
        WHERE o.word = :word AND o.languageId = :languageId
            """)
    void updateStatus(@Param("word") String word, @Param("languageId") Long languageId);
}
