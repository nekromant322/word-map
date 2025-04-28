package com.margot.word_map.repository.map;

import com.margot.word_map.model.map.World;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorldRepository extends JpaRepository<World, Long> {

    Optional<World> findByActiveIsTrueAndLanguageAndPlatform(String language, String platform);

    Optional<World> findByGridId(Long id);
}
