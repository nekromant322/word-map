package com.margot.word_map.repository;

import com.margot.word_map.model.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformRepository extends JpaRepository <Platform, Long> {
    boolean existsByName(String name);

    @Query("""
            SELECT COUNT(p) > 0 FROM Platform p
            WHERE p.name = :name
            AND p.id != :id
            """)
    boolean existsByNameExcludeId(String name, Long id);
}
