package com.margot.word_map.repository;

import com.margot.word_map.model.Server;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRepository extends JpaRepository<Server, Long> {

    boolean existsByPlatformIdAndLanguageIdAndIsOpenTrue(Long platformId, Long languageId);
}
