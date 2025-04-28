package com.margot.word_map.repository.map;

import com.margot.word_map.model.map.World;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorldRepository extends JpaRepository<World, Long> {

    World findByGridId(Long id);
}
