package com.margot.word_map.repository;

import com.margot.word_map.map.MapTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MapTitlesRepository extends JpaRepository<MapTitle, Long> {
}
