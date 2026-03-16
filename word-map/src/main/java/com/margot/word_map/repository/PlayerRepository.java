package com.margot.word_map.repository;

import com.margot.word_map.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByUuid(String uuid);

    boolean existsByLanguage_Id(Long languageId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Player p WHERE p.server.id = :serverId")
    void deleteByServerId(Long serverId);
}
