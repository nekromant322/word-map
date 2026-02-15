package com.margot.word_map.repository;

import com.margot.word_map.model.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatternRepository extends JpaRepository<Pattern, Long> {

    Page<Pattern> findAll(Specification<Pattern> spec, Pageable pageable);
}
