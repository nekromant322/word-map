package com.margot.word_map.repository;

import com.margot.word_map.model.AdminLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminLanguageRepository extends JpaRepository<AdminLanguage, Long> {

    Optional<AdminLanguage> findByAdminIdAndLanguageId(Long adminId, Long langId);
}
