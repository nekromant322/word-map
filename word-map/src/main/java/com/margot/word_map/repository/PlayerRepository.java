package com.margot.word_map.repository;

import com.margot.word_map.dto.response.PlayerDetailedResponse;
import com.margot.word_map.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByUuid(String uuid);

    @Query("""
    SELECT new com.margot.word_map.dto.response.PlayerDetailedResponse(
        p.id, 
        p.uuid, 
        p.name, 
        p.platform.name, 
        p.language.prefix, 
        p.createdAt, 
        p.activeAt, 
        p.score, 
        p.access, 
        (SELECT COUNT(o) FROM WordOffer o WHERE o.playerId = p.id)
    )
    FROM Player p
    WHERE p.id = :playerId
                """)
    Optional<PlayerDetailedResponse> findDetailedPlayer(@Param("playerId") Long playerId);
}
