package com.margot.word_map.repository.map;

import com.margot.word_map.model.map.Grid;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface GridRepository extends JpaRepository<Grid, Long> {
    Optional<Grid> findByPoint(Point point);

    @Modifying
    @Transactional
    @Query("DELETE FROM Grid g WHERE g.server.id = :serverId")
    void deleteByServerId(Long serverId);
}
