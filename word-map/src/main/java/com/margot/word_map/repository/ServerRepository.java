package com.margot.word_map.repository;

import com.margot.word_map.model.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServerRepository extends JpaRepository<Server, Long> {

    boolean existsByPlatformIdAndLanguageIdAndIsOpenTrue(Long platformId, Long languageId);

    @Modifying
    @Query("""
                update Server s
                set s.cleanupInProgress = true
                where s.id = :id
                  and s.cleanupInProgress = false
                  and s.isOpen = true
            """)
    int markCleanupStarted(@Param("id") Long id);
}
