package com.margot.word_map.repository;

import com.margot.word_map.map.MapTile;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MapTileRepository extends JpaRepository<MapTile, Long> {
    Optional<MapTile> findByPoint(Point point);
}
