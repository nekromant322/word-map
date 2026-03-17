package com.margot.word_map.repository;

import com.margot.word_map.dto.LeaderboardRowDto;
import com.margot.word_map.model.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {

    @Query("""
                SELECT new com.margot.word_map.dto.LeaderboardRowDto(
                    l.player.id, 
                    l.player.name, 
                    SUM(l.score), 
                    MIN(l.updatedAt), 
                    l.player.access
                )
                FROM Leaderboard l
                WHERE l.server.platform.name = :platformId 
                  AND l.server.language.id = :languageId
                  AND (CAST(:start AS timestamp) IS NULL OR l.updatedAt >= :start)
                  AND (CAST(:end AS timestamp) IS NULL OR l.updatedAt <= :end)
                GROUP BY l.player.id, l.player.name, l.player.access
                ORDER BY SUM(l.score) DESC, MIN(l.updatedAt) ASC
            """)
    List<LeaderboardRowDto> findPlayersInLeaderboard(
            @Param("platformId") String platformId,
            @Param("languageId") Long languageId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
