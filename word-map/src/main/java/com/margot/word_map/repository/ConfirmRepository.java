package com.margot.word_map.repository;

import com.margot.word_map.model.Confirm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmRepository extends JpaRepository<Confirm, Long> {
    Optional<Confirm> findByUserIdAndCode(Long userId, Integer code);
    Optional<Confirm> findByUserId(Long userId);
}
