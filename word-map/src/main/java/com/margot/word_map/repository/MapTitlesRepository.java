package com.margot.word_map.repository;

import com.margot.word_map.map.MapTitle;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MapTitlesRepository extends JpaRepository<MapTitle, Long> {
    Optional<MapTitle> findByPoint(Point point);
}
