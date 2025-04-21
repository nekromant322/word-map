package com.margot.word_map.repository.map;

import com.margot.word_map.model.map.Tile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TileRepository extends JpaRepository<Tile, Short> {
}
