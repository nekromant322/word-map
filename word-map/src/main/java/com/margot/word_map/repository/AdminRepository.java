package com.margot.word_map.repository;

import com.margot.word_map.model.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);

    Page<Admin> findAll(Specification<Admin> spec, Pageable pageable);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = "languages.language")
    Optional<Admin> findWithLangById(Long adminId);
}
