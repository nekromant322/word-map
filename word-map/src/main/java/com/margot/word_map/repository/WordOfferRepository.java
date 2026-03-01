package com.margot.word_map.repository;

import com.margot.word_map.model.WordOffer;
import com.margot.word_map.model.WordOfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordOfferRepository extends JpaRepository<WordOffer, Long>, JpaSpecificationExecutor<WordOffer> {

    Optional<WordOffer> findOfferByWordAndPlayerId(String word, Long playerId);

    Optional<WordOffer> findOfferByWordAndLanguageId(String word, Long languageId);

    @Query("SELECT DISTINCT wo.status" +
            " FROM WordOffer wo" +
            " WHERE wo.word = :word" +
            " AND wo.languageId = :languageId")
    List<WordOfferStatus> findDistinctStatusByWordAndLanguageId(String word, Long languageId);

    @Modifying
    @Query("""
        UPDATE WordOffer o
        SET o.status = :status
        WHERE o.word = :word AND o.languageId = :languageId
            """)
    void updateStatus(@Param("word") String word,
                      @Param("languageId") Long languageId,
                      @Param("status") WordOfferStatus status);
}
